package a_rewrite.generator;

import a_rewrite.config.Configuration;
import a_rewrite.util.TypeDBUtil;
import a_rewrite.util.Util;
import com.vaticle.typedb.client.api.connection.TypeDBClient;
import com.vaticle.typedb.client.api.connection.TypeDBSession;
import com.vaticle.typeql.lang.query.TypeQLInsert;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class AppendAttributeGeneratorTest {

    @Test
    public void phoneCallsPersonTest() throws IOException {
        String dbName = "append-attribute-generator-test";
        String sp = new File("src/test/resources/1.0.0/phoneCalls/schema.gql").getAbsolutePath();
        TypeDBClient client = TypeDBUtil.getClient("localhost:1729");
        TypeDBUtil.cleanAndDefineSchemaToDatabase(client, dbName, sp);

        String dcp = new File("src/test/resources/1.0.0/phoneCalls/dc.json").getAbsolutePath();
        Configuration dc = Util.initializeDataConfig(dcp);
        assert dc != null;
        ArrayList<String> appendKeys = new ArrayList<>(List.of("append-twitter", "append-fakebook", "append-call-rating"));
        TypeDBSession session = TypeDBUtil.getDataSession(client, dbName);
        for (String appendkey : appendKeys) {
            if (dc.getAppendAttribute().get(appendkey).getAttributes() != null) {
                for (int idx = 0; idx < dc.getAppendAttribute().get(appendkey).getAttributes().length; idx++) {
                    Util.setAppendAttributeHasAttributeConceptType(dc.getAppendAttribute().get(appendkey), idx, session);
                }
            }
            if (dc.getAppendAttribute().get(appendkey).getThingGetter() != null && dc.getAppendAttribute().get(appendkey).getThingGetter().getThingGetters() != null) {
                for (int idx = 0; idx < dc.getAppendAttribute().get(appendkey).getThingGetter().getThingGetters().length; idx++) {
                    Util.setThingGetterEntityHasAttributeConceptType(dc.getAppendAttribute().get(appendkey).getThingGetter().getThingGetters()[idx], session);
                }
            }
        }
        session.close();
        client.close();

        testTwitter(dc, appendKeys);
        testFakebook(dc, appendKeys);
        testCallAppend(dc, appendKeys);
    }

    private void testTwitter(Configuration dc, ArrayList<String> appendKeys) throws IOException {
        String dp = new File("src/test/resources/1.0.0/phoneCalls/append-twitter-nickname.csv").getAbsolutePath();
        AppendAttributeGenerator gen = new AppendAttributeGenerator(dp,
                dc.getAppendAttribute().get(appendKeys.get(0)),
                Objects.requireNonNullElseGet(dc.getAppendAttribute().get(appendKeys.get(0)).getSeparator(), () -> dc.getDefaultConfig().getSeparator()));
        Iterator<String> iterator = Util.newBufferedReader(dp).lines().skip(1).iterator();

        TypeQLInsert statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        String tmp = "match $thing isa person, has phone-number \"+7 171 898 0853\";\n" +
                "insert $thing has twitter-username \"@jojo\", has nick-name \"another\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.appendAttributeInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $thing isa person, has phone-number \"+263 498 495 0617\";\n" +
                "insert $thing has twitter-username \"@hui\", has twitter-username \"@bui\", has nick-name \"yetanoter\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.appendAttributeInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $thing isa person, has phone-number \"+370 351 224 5176\";\n" +
                "insert $thing has twitter-username \"@lalulix\", has nick-name \"one more\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.appendAttributeInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $thing isa person, has phone-number \"+81 308 988 7153\";\n" +
                "insert $thing has twitter-username \"@go34\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.appendAttributeInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $thing isa person, has phone-number \"+54 398 559 0423\";\n" +
                "insert $thing has twitter-username \"@hadaaa\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.appendAttributeInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $thing isa person, has phone-number \"+7 690 597 4443\";\n" +
                "insert $thing has nick-name \"not inserted\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.appendAttributeInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $thing isa person, has phone-number \"+63 815 962 6097\";\n" +
                "insert $thing has twitter-username \"@kuka\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.appendAttributeInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "insert $null isa null, has null \"null\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.appendAttributeInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $thing isa person;\n" +
                "insert $thing has twitter-username \"@notinserted\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.appendAttributeInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "insert $null isa null, has null \"null\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.appendAttributeInsertStatementValid(statement));

    }

    private void testFakebook(Configuration dc, ArrayList<String> appendKeys) throws IOException {
        String dp = new File("src/test/resources/1.0.0/phoneCalls/append-fb-preprocessed.csv").getAbsolutePath();
        AppendAttributeGenerator gen = new AppendAttributeGenerator(dp,
                dc.getAppendAttribute().get(appendKeys.get(1)),
                Objects.requireNonNullElseGet(dc.getAppendAttribute().get(appendKeys.get(1)).getSeparator(), () -> dc.getDefaultConfig().getSeparator()));
        Iterator<String> iterator = Util.newBufferedReader(dp).lines().skip(1).iterator();

        TypeQLInsert statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        String tmp = "match $thing isa person, has phone-number \"+36 318 105 5629\";\n" +
                "insert $thing has fakebook-link \"fakebook.com/personOne\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.appendAttributeInsertStatementValid(statement));

        iterator.next();
        iterator.next();

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $thing isa person, has phone-number \"+62 533 266 3426\";\n" +
                "insert $thing has fakebook-link \"insertedWithoutAppliedRegex\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.appendAttributeInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $thing isa person;\n" +
                "insert $thing has fakebook-link \"@notinserted\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.appendAttributeInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "insert $null isa null, has null \"null\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.appendAttributeInsertStatementValid(statement));
    }

    private void testCallAppend(Configuration dc, ArrayList<String> appendKeys) throws IOException {
        String dp = new File("src/test/resources/1.0.0/phoneCalls/append-call-rating.csv").getAbsolutePath();
        AppendAttributeGenerator gen = new AppendAttributeGenerator(dp,
                dc.getAppendAttribute().get(appendKeys.get(2)),
                Objects.requireNonNullElseGet(dc.getAppendAttribute().get(appendKeys.get(2)).getSeparator(), () -> dc.getDefaultConfig().getSeparator()));
        Iterator<String> iterator = Util.newBufferedReader(dp).lines().skip(1).iterator();

        TypeQLInsert statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        String tmp = "match $thing isa call, has started-at 2018-09-19T01:00:38;\n" +
                "insert $thing has call-rating 5;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.appendAttributeInsertStatementValid(statement));

        iterator.next();
        iterator.next();
        iterator.next();
        iterator.next();

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "insert $null isa null, has null \"null\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.appendAttributeInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $thing isa call;\n" +
                "insert $thing has call-rating 4;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.appendAttributeInsertStatementValid(statement));
    }
}