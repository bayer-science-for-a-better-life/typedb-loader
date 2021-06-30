package schema;

import com.vaticle.typedb.client.api.connection.TypeDBClient;
import configuration.LoaderSchemaUpdateConfig;
import write.TypeDBWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TypeDBSchemaUpdater {

    private static final Logger appLogger = LogManager.getLogger("com.bayer.dt.grami");
    private final TypeDBWriter gm;

    public TypeDBSchemaUpdater(LoaderSchemaUpdateConfig suConfig) {
        this.gm = new TypeDBWriter(suConfig.getGraknURI().split(":")[0],
                suConfig.getGraknURI().split(":")[1],
                suConfig.getSchemaPath(),
                suConfig.getKeyspace()
        );
    }

    public void updateSchema() {
        TypeDBClient client = gm.getClient();
        appLogger.info("applying schema to existing schema");
        gm.loadAndDefineSchema(client);
        appLogger.info("GraMi is finished applying your schema!");
        client.close();
    }
}