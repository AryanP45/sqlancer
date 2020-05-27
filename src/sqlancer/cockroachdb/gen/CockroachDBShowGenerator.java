package sqlancer.cockroachdb.gen;

import java.util.HashSet;
import java.util.Set;

import sqlancer.Query;
import sqlancer.QueryAdapter;
import sqlancer.Randomly;
import sqlancer.cockroachdb.CockroachDBErrors;
import sqlancer.cockroachdb.CockroachDBProvider.CockroachDBGlobalState;

public class CockroachDBShowGenerator {

    private CockroachDBShowGenerator() {
    }

    private enum Option {
        EXPERIMENTAL_FINGERPRINTS, // https://github.com/cockroachdb/cockroach/issues/44237
        DATABASES, JOBS, RANGES, LOCALITY, SEQUENCES, TRACE_FOR_SESSION
    }

    public static Query show(CockroachDBGlobalState globalState) {
        Set<String> errors = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        switch (Randomly.fromOptions(Option.values())) {
        case EXPERIMENTAL_FINGERPRINTS:
            sb.append("SHOW EXPERIMENTAL_FINGERPRINTS FROM TABLE "
                    + globalState.getSchema().getRandomTable(t -> !t.isView()).getName());
            errors.add("as type bytes: bytea encoded value ends with incomplete escape sequence");
            errors.add("invalid bytea escape sequence");
            break;
        case DATABASES:
            sb.append("SHOW DATABASES");
            break;
        case JOBS:
            sb.append("SHOW JOBS");
            break;
        case RANGES:
            sb.append("SHOW RANGES FROM TABLE " + globalState.getSchema().getRandomTable(t -> !t.isView()).getName());
            break;
        case LOCALITY:
            sb.append("SHOW LOCALITY");
            break;
        case SEQUENCES:
            sb.append("SHOW SEQUENCES");
            break;
        case TRACE_FOR_SESSION:
            sb.append("SHOW ");
            if (Randomly.getBoolean()) {
                sb.append("COMPACT ");
            }
            if (Randomly.getBoolean()) {
                sb.append("KV ");
            }
            sb.append("TRACE FOR SESSION;");
            break;
        default:
            throw new AssertionError();
        }
        CockroachDBErrors.addTransactionErrors(errors);
        return new QueryAdapter(sb.toString(), errors);
    }

}
