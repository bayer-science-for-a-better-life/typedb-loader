package generator;

import config.Configuration;
import io.FileLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.GeneratorUtil;
import util.Util;
import com.vaticle.typedb.client.api.answer.ConceptMap;
import com.vaticle.typedb.client.api.connection.TypeDBTransaction;
import com.vaticle.typedb.client.common.exception.TypeDBClientException;
import com.vaticle.typeql.lang.TypeQL;
import com.vaticle.typeql.lang.pattern.constraint.ThingConstraint;
import com.vaticle.typeql.lang.pattern.variable.ThingVariable;
import com.vaticle.typeql.lang.pattern.variable.UnboundVariable;
import com.vaticle.typeql.lang.query.TypeQLInsert;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import static util.GeneratorUtil.constrainThingWithHasAttributes;

public class AppendAttributeOrInsertThingGenerator implements Generator {
    private static final Logger dataLogger = LogManager.getLogger("com.bayer.dt.tdl.error");
    private final String filePath;
    private final String[] header;
    private final Configuration.AppendAttributeOrInsertThing appendOrInsertConfiguration;
    private final char fileSeparator;

    public AppendAttributeOrInsertThingGenerator(String filePath, Configuration.AppendAttributeOrInsertThing appendOrInsertConfiguration, char fileSeparator) throws IOException {
        this.filePath = filePath;
        this.header = Util.getFileHeader(filePath, fileSeparator);
        this.appendOrInsertConfiguration = appendOrInsertConfiguration;
        this.fileSeparator = fileSeparator;
    }

    public void write(TypeDBTransaction tx,
                      String[] row) {
        String fileName = FilenameUtils.getName(filePath);
        String fileNoExtension = FilenameUtils.removeExtension(fileName);
        String originalRow = String.join(Character.toString(fileSeparator), row);

        if (row.length > header.length) {
            FileLogger.getLogger().logMalformed(fileName, originalRow);
            dataLogger.error("Malformed Row detected in <" + filePath + "> - written to <" + fileNoExtension + "_malformed.log" + ">");
        }

        TypeQLInsert appendStatement = generateMatchInsertStatement(row);
        TypeQLInsert insertStatement = generateThingInsertStatement(row);

        if (appendAttributeInsertStatementValid(appendStatement)) {
            try {
                final Stream<ConceptMap> insertedStream = tx.query().insert(appendStatement);
                if (insertedStream.count() == 0) {
                    if (thingInsertStatementValid(insertStatement)) {
                        tx.query().insert(insertStatement);
                    } else {
                        FileLogger.getLogger().logInvalid(fileName, originalRow);
                        dataLogger.error("Invalid Row detected in <" + filePath + "> - written to <" + fileNoExtension + "_invalid.log" + "> - invalid Statement: <" + insertStatement.toString().replace("\n", " ") + ">");
                    }
                }
            } catch (TypeDBClientException typeDBClientException) {
                FileLogger.getLogger().logUnavailable(fileName, originalRow);
                dataLogger.error("TypeDB Unavailable - Row in <" + filePath + "> not inserted - written to <" + fileNoExtension + "_unavailable.log" + ">");
            }
        } else {
            if (thingInsertStatementValid(insertStatement)) {
                tx.query().insert(insertStatement);
            } else {
                FileLogger.getLogger().logInvalid(fileName, originalRow);
                dataLogger.error("Invalid Row detected in <" + filePath + "> - written to <" + fileNoExtension + "_invalid.log" + "> - invalid Statements: <" + appendStatement.toString().replace("\n", " ") + "> and <" + insertStatement.toString().replace("\n", " ") + ">");
            }
        }
    }

    public TypeQLInsert generateMatchInsertStatement(String[] row) {
        if (row.length > 0) {
            ThingVariable.Thing entityMatchStatement = TypeQL.var("thing")
                    .isa(appendOrInsertConfiguration.getThingGetter().getConceptType());
            for (Configuration.ThingGetter ownershipThingGetter : appendOrInsertConfiguration.getThingGetter().getThingGetters()) {
                ArrayList<ThingConstraint.Value<?>> constraintValues = GeneratorUtil.generateValueConstraintsConstrainingAttribute(
                        row, header, filePath, fileSeparator, ownershipThingGetter);
                for (ThingConstraint.Value<?> constraintValue : constraintValues) {
                    entityMatchStatement.constrain(GeneratorUtil.valueToHasConstraint(ownershipThingGetter.getConceptType(), constraintValue));
                }
            }

            UnboundVariable insertUnboundVar = TypeQL.var("thing");
            ThingVariable.Thing insertStatement = null;
            for (Configuration.ConstrainingAttribute attributeToAppend : appendOrInsertConfiguration.getAttributes()) {
                ArrayList<ThingConstraint.Value<?>> constraintValues = GeneratorUtil.generateValueConstraintsConstrainingAttribute(
                        row, header, filePath, fileSeparator, attributeToAppend);
                for (ThingConstraint.Value<?> constraintValue : constraintValues) {
                    if (insertStatement == null) {
                        insertStatement = insertUnboundVar.constrain(GeneratorUtil.valueToHasConstraint(attributeToAppend.getConceptType(), constraintValue));
                    } else {
                        insertStatement.constrain(GeneratorUtil.valueToHasConstraint(attributeToAppend.getConceptType(), constraintValue));
                    }
                }
            }

            if (insertStatement != null) {
                return TypeQL.match(entityMatchStatement).insert(insertStatement);
            } else {
                return TypeQL.insert(TypeQL.var("null").isa("null").has("null", "null"));
            }
        } else {
            return TypeQL.insert(TypeQL.var("null").isa("null").has("null", "null"));
        }
    }

    public TypeQLInsert generateThingInsertStatement(String[] row) {
        if (row.length > 0) {
            ThingVariable.Thing insertStatement = GeneratorUtil.generateBoundThingVar(appendOrInsertConfiguration.getThingGetter().getConceptType());

            for (Configuration.ConstrainingAttribute constrainingAttribute : appendOrInsertConfiguration.getThingGetter().getOwnershipThingGetters()) {
                ArrayList<ThingConstraint.Value<?>> constraintValues = GeneratorUtil.generateValueConstraintsConstrainingAttribute(
                        row, header, filePath, fileSeparator, constrainingAttribute);
                for (ThingConstraint.Value<?> constraintValue : constraintValues) {
                    insertStatement.constrain(GeneratorUtil.valueToHasConstraint(constrainingAttribute.getConceptType(), constraintValue));
                }
            }

            constrainThingWithHasAttributes(row, header, filePath, fileSeparator, insertStatement, appendOrInsertConfiguration.getAttributes());

            return TypeQL.insert(insertStatement);
        } else {
            return TypeQL.insert(TypeQL.var("null").isa("null").has("null", "null"));
        }
    }

    public boolean appendAttributeInsertStatementValid(TypeQLInsert insert) {
        if (insert == null) return false;
        if (!insert.toString().contains("isa " + appendOrInsertConfiguration.getThingGetter().getConceptType())) return false;
        for (Configuration.ConstrainingAttribute ownershipThingGetter : appendOrInsertConfiguration.getThingGetter().getOwnershipThingGetters()) {
            if (!insert.toString().contains(", has " + ownershipThingGetter.getConceptType())) return false;
        }
        if (appendOrInsertConfiguration.getRequireNonEmptyAttributes() != null) {
            for (Configuration.ConstrainingAttribute constrainingAttribute : appendOrInsertConfiguration.getRequireNonEmptyAttributes()) {
                if (!insert.toString().contains("has " + constrainingAttribute.getConceptType())) return false;
            }
        }
        return true;
    }

    public boolean thingInsertStatementValid(TypeQLInsert insert) {
        if (insert == null) return false;
        if (!insert.toString().contains("isa " + appendOrInsertConfiguration.getThingGetter().getConceptType())) return false;
        for (Configuration.ConstrainingAttribute ownershipThingGetter : appendOrInsertConfiguration.getThingGetter().getOwnershipThingGetters()) {
            if (ownershipThingGetter.getRequireNonEmpty() != null && ownershipThingGetter.getRequireNonEmpty()) {
                if (!insert.toString().contains(", has " + ownershipThingGetter.getConceptType())) return false;
            }
        }
        if (appendOrInsertConfiguration.getRequireNonEmptyAttributes() != null) {
            for (Configuration.ConstrainingAttribute constrainingAttribute : appendOrInsertConfiguration.getRequireNonEmptyAttributes()) {
                if (!insert.toString().contains("has " + constrainingAttribute.getConceptType())) return false;
            }
        }
        return true;
    }

    public char getFileSeparator() {
        return this.fileSeparator;
    }
}
