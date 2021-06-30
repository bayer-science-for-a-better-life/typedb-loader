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

public class RelationGeneratorTest {

    @Test
    public void genericRelationTest() throws IOException {

        String dbName = "relation-generator-test";
        String sp = new File("src/test/resources/1.0.0/generic/schema.gql").getAbsolutePath();
        TypeDBClient client = TypeDBUtil.getClient("localhost:1729");
        TypeDBUtil.cleanAndDefineSchemaToDatabase(client, dbName, sp);

        String dcp = new File("src/test/resources/1.0.0/generic/dc.json").getAbsolutePath();
        Configuration dc = Util.initializeDataConfig(dcp);
        assert dc != null;
        ArrayList<String> relationKeys = new ArrayList<>(List.of("rel1"));
        TypeDBSession session = TypeDBUtil.getDataSession(client, dbName);
        for (String relationKey : relationKeys) {
            if (dc.getRelations().get(relationKey).getAttributes() != null) {
                for (int idx = 0; idx < dc.getRelations().get(relationKey).getAttributes().length; idx++) {
                    Util.setRelationHasAttributeConceptType(dc.getRelations().get(relationKey), idx, session);
                }
            }
            for (int idx = 0; idx < dc.getRelations().get(relationKey).getPlayers().length; idx++) {
                Util.setGetterAttributeConceptType(dc.getRelations().get(relationKey), idx, session);
            }
        }
        session.close();
        client.close();

        String dp = new File("src/test/resources/1.0.0/generic/rel1.tsv").getAbsolutePath();
        RelationGenerator gen = new RelationGenerator(dp,
                dc.getRelations().get(relationKeys.get(0)),
                Objects.requireNonNullElseGet(dc.getRelations().get(relationKeys.get(0)).getSeparator(), () -> dc.getDefaultConfig().getSeparator()));
        Iterator<String> iterator = Util.newBufferedReader(dp).lines().skip(1).iterator();

        TypeQLInsert statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        String tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att0\", has relAt-1 \"explosion0\", has relAt-2 \"opt0\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att1\", has relAt-1 \"explosion1\", has relAt-1 \"explo1\", has relAt-2 \"opt1\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att2\", has relAt-2 \"opt2\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att3\", has relAt-2 \"opt3\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att4\", has relAt-2 \"opt4\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att5\", has relAt-2 \"opt5\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att6\", has relAt-2 \"opt6\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att7\", has relAt-2 \"opt7\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att8\", has relAt-2 \"opt8\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att9\", has relAt-2 \"opt9\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att10\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1) isa rel1, has relAt-1 \"att19\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1) isa rel1, has relAt-1 \"att20\", has relAt-2 \"opt20\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1) isa rel1, has relAt-1 \"att21\", has relAt-1 \"explosion21\", has relAt-2 \"optional21\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att22\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-2 \"opt25\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-1 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-two: $player-0, player-optional: $player-1) isa rel1, has relAt-1 \"att34\", has relAt-2 \"opt33\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\";\n" +
                "$player-1 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-optional: $player-1) isa rel1, has relAt-1 \"att37\", has relAt-2 \"opt36\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\", has entity1-id \"entity1id2\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-2 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1, player-optional: $player-2) isa rel1, has relAt-1 \"att39\", has relAt-2 \"opt39\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity1, has entity1-id \"entity1id1\", has entity1-id \"entity1id2\";\n" +
                "$player-1 isa entity2, has entity2-id \"entity2id1\";\n" +
                "insert $rel (player-one: $player-0, player-two: $player-1) isa rel1, has relAt-1 \"att40\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseTSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa entity2, has entity2-id \"entity2id1\";\n" +
                "$player-1 isa entity3, has entity3-id \"entity3id1\";\n" +
                "insert $rel (player-two: $player-0, player-optional: $player-1) isa rel1, has relAt-1 \"att41\", has relAt-2 \"opt41\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

    }

    @Test
    public void phoneCallsPersonTest() throws IOException {
        String dbName = "relation-generator-test";
        String sp = new File("src/test/resources/1.0.0/phoneCalls/schema.gql").getAbsolutePath();
        TypeDBClient client = TypeDBUtil.getClient("localhost:1729");
        TypeDBUtil.cleanAndDefineSchemaToDatabase(client, dbName, sp);

        String dcp = new File("src/test/resources/1.0.0/phoneCalls/dc.json").getAbsolutePath();
        Configuration dc = Util.initializeDataConfig(dcp);
        assert dc != null;
        ArrayList<String> relationKeys = new ArrayList<>(List.of("contract", "call", "in-use"));
        TypeDBSession session = TypeDBUtil.getDataSession(client, dbName);
        for (String relationKey : relationKeys) {
            if (dc.getRelations().get(relationKey).getAttributes() != null) {
                for (int idx = 0; idx < dc.getRelations().get(relationKey).getAttributes().length; idx++) {
                    Util.setRelationHasAttributeConceptType(dc.getRelations().get(relationKey), idx, session);
                }
            }
            for (int idx = 0; idx < dc.getRelations().get(relationKey).getPlayers().length; idx++) {
                Util.setGetterAttributeConceptType(dc.getRelations().get(relationKey), idx, session);
            }
        }
        session.close();
        client.close();

        testContracts(dc, relationKeys);
        testCalls(dc, relationKeys);
        testInUse(dc, relationKeys);
    }

    private void testContracts(Configuration dc, ArrayList<String> relationKeys) throws IOException {
        String dp = new File("src/test/resources/1.0.0/phoneCalls/contract.csv").getAbsolutePath();
        RelationGenerator gen = new RelationGenerator(dp,
                dc.getRelations().get(relationKeys.get(0)),
                Objects.requireNonNullElseGet(dc.getRelations().get(relationKeys.get(0)).getSeparator(), () -> dc.getDefaultConfig().getSeparator()));
        Iterator<String> iterator = Util.newBufferedReader(dp).lines().skip(1).iterator();

        TypeQLInsert statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        String tmp = "match\n" +
                "$player-0 isa company, has name \"Telecom\";\n" +
                "$player-1 isa person, has phone-number \"+7 171 898 0853\";\n" +
                "insert $rel (provider: $player-0, customer: $player-1) isa contract;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        for (int i = 0; i < 7; i++) {
            iterator.next();
        }

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "insert $null isa null, has null \"null\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        iterator.next();

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $player-0 isa person, has phone-number \"+261 860 539 4754\";\n" +
                "insert $rel (customer: $player-0) isa contract;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $player-0 isa company, has name \"Telecom\";\n" +
                "insert $rel (provider: $player-0) isa contract;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        for (int i = 0; i < 3; i++) {
            iterator.next();
        }

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa company, has name \"Telecom\";\n" +
                "$player-1 isa person, has phone-number \"+62 107 530 7500\", has phone-number \"+261 860 539 4754\";\n" +
                "insert $rel (provider: $player-0, customer: $player-1) isa contract;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));
    }

    private void testCalls(Configuration dc, ArrayList<String> relationKeys) throws IOException {

        String dp = new File("src/test/resources/1.0.0/phoneCalls/call.csv").getAbsolutePath();
        RelationGenerator gen = new RelationGenerator(dp,
                dc.getRelations().get(relationKeys.get(1)),
                Objects.requireNonNullElseGet(dc.getRelations().get(relationKeys.get(1)).getSeparator(), () -> dc.getDefaultConfig().getSeparator()));
        Iterator<String> iterator = Util.newBufferedReader(dp).lines().skip(1).iterator();

        TypeQLInsert statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        String tmp = "match\n" +
                "$player-0 isa person, has phone-number \"+54 398 559 0423\";\n" +
                "$player-1 isa person, has phone-number \"+48 195 624 2025\";\n" +
                "insert $rel (caller: $player-0, callee: $player-1) isa call, has started-at 2018-09-16T22:24:19, has duration 122;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        for (int i = 0; i < 112; i++) {
            iterator.next();
        }

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa person, has phone-number \"+63 815 962 6097\";\n" +
                "$player-1 isa person, has phone-number \"+263 498 495 0617\";\n" +
                "insert $rel (caller: $player-0, callee: $player-1) isa call, has started-at 2018-09-19T23:16:49;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        for (int i = 0; i < 98; i++) {
            iterator.next();
        }

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa person, has phone-number \"+63 815 962 6097\";\n" +
                "$player-1 isa person, has phone-number \"+7 552 196 4096\";\n" +
                "insert $rel (caller: $player-0, callee: $player-1) isa call, has started-at 2018-09-23T01:14:56;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa person, has phone-number \"+63 815 962 6097\";\n" +
                "$player-1 isa person, has phone-number \"+7 552 196 4096\";\n" +
                "insert $rel (caller: $player-0, callee: $player-1) isa call, has started-at 2018-09-23T01:14:56;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match\n" +
                "$player-0 isa person, has phone-number \"+63 815 962 6097\";\n" +
                "$player-1 isa person, has phone-number \"+7 552 196 4096\";\n" +
                "insert $rel (caller: $player-0, callee: $player-1) isa call, has duration 53;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $player-0 isa person, has phone-number \"+63 815 962 6097\";\n" +
                "insert $rel (caller: $player-0) isa call, has started-at 2018-09-23T01:14:56, has duration 53;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "insert $null isa null, has null \"null\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $player-0 isa person, has phone-number \"+7 552 196 4096\";\n" +
                "insert $rel (callee: $player-0) isa call, has started-at 2018-09-23T01:14:56, has duration 53;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));
    }

    private void testInUse(Configuration dc, ArrayList<String> relationKeys) throws IOException {
        String dp = new File("src/test/resources/1.0.0/phoneCalls/in-use.csv").getAbsolutePath();
        RelationGenerator gen = new RelationGenerator(dp,
                dc.getRelations().get(relationKeys.get(2)),
                Objects.requireNonNullElseGet(dc.getRelations().get(relationKeys.get(2)).getSeparator(), () -> dc.getDefaultConfig().getSeparator()));
        Iterator<String> iterator = Util.newBufferedReader(dp).lines().skip(1).iterator();

        TypeQLInsert statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        String tmp = "match\n" +
                "$player-0 \"yes\" isa is-in-use;\n" +
                "$player-1 \"+7 171 898 0853\" isa phone-number;\n" +
                "insert $rel (status: $player-0, account: $player-1) isa in-use;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertTrue(gen.relationInsertStatementValid(statement));

        for (int i = 0; i < 4; i++) {
            iterator.next();
        }

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "insert $null isa null, has null \"null\";";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));

        for (int i = 0; i < 2; i++) {
            iterator.next();
        }

        statement = gen.generateMatchInsertStatement(Util.parseCSV(iterator.next()));
        tmp = "match $player-0 \"+62 107 530 7500\" isa phone-number;\n" +
                "insert $rel (account: $player-0) isa in-use;";
        Assert.assertEquals(tmp, statement.toString());
        Assert.assertFalse(gen.relationInsertStatementValid(statement));


    }

}