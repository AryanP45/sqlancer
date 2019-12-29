package lama.sqlite3.ast;

import java.util.List;

import lama.IgnoreMeException;
import lama.Randomly;
import lama.sqlite3.SQLite3Provider;
import lama.sqlite3.SQLite3Provider.SQLite3GlobalState;
import lama.sqlite3.gen.SQLite3ExpressionGenerator;
import lama.sqlite3.schema.SQLite3Schema.SQLite3Column;
import lama.sqlite3.schema.SQLite3Schema.SQLite3Column.CollateSequence;

public class SQLite3WindowFunction extends SQLite3Expression {


	private WindowFunction func;
	private SQLite3Expression[] args;
	
	public static SQLite3WindowFunction getRandom(List<SQLite3Column> columns, SQLite3GlobalState globalState) {
		WindowFunction func = Randomly.fromOptions(WindowFunction.values());
		SQLite3Expression[] args = new SQLite3Expression[func.nrArgs];
		for (int i = 0; i < args.length; i++) {
			args[i] = new SQLite3ExpressionGenerator(globalState).setColumns(columns).getRandomExpression();
		}
		return new SQLite3WindowFunction(func, args);
	}

	public static enum WindowFunction {
		
		ROW_NUMBER {
			@Override
			public SQLite3Constant apply(SQLite3Constant... args) {
				return SQLite3Constant.createIntConstant(1);
			}
		
		}, 
		RANK {
			@Override
			public SQLite3Constant apply(SQLite3Constant... args) {
				return SQLite3Constant.createIntConstant(1);
			}
		},
		DENSE_RANK {
			@Override
			public SQLite3Constant apply(SQLite3Constant... args) {
				return SQLite3Constant.createIntConstant(1);
			}
		},
		PERCENT_RANK {
			@Override
			public SQLite3Constant apply(SQLite3Constant... args) {
				return SQLite3Constant.createRealConstant(0.0);
			}
		},
		CUME_DIST {
			@Override
			public SQLite3Constant apply(SQLite3Constant... args) {
				return SQLite3Constant.createRealConstant(1.0);
			}
		},
		NTILE(1),//
		LAG(3), //
		LEAD(3), //
		FIRST_VALUE(1) {
			
			@Override
			public SQLite3Constant apply(SQLite3Constant... args) {
				return args[0];
			}
		},
		LAST_VALUE(1) {
			@Override
			public SQLite3Constant apply(SQLite3Constant... args) {
				return args[0];
			}
		},
		NTH_VALUE(2) {
			@Override
			public SQLite3Constant apply(SQLite3Constant... args) {
				SQLite3Constant n = SQLite3Cast.castToInt(args[1]);
				if (!n.isNull() && n.asInt() == 1) {
					return args[0];
				} else {
					return SQLite3Constant.createNullConstant();
				}
			}
		}
		;
		
		
		int nrArgs;
		
		WindowFunction(int nrArgs) {
			this.nrArgs = nrArgs;
		};
		
		private WindowFunction() {
			this(0);
		}
		
		
		public SQLite3Constant apply(SQLite3Constant... args) {
			if (SQLite3Provider.MUST_KNOW_RESULT) {
				throw new AssertionError();
			}
			return null;
		}
		
		public int getNrArgs() {
			return nrArgs;
		}
	}
	
	public SQLite3WindowFunction(WindowFunction func, SQLite3Expression[] args) {
		this.func = func;
		this.args = args;
	}

	public WindowFunction getFunc() {
		return func;
	}
	
	public SQLite3Expression[] getArgs() {
		return args;
	}
	
	@Override
	public CollateSequence getExplicitCollateSequence() {
		return null;
	}
	
	@Override
	public SQLite3Constant getExpectedValue() {
		if (!SQLite3Provider.MUST_KNOW_RESULT) {
			return null;
		}
		SQLite3Constant[] evaluatedConst = new SQLite3Constant[args.length];
		for (int i = 0; i < evaluatedConst.length; i++) {
			evaluatedConst[i] = args[i].getExpectedValue();
			if (evaluatedConst[i] == null) {
				throw new IgnoreMeException();
			}
		}
		return func.apply(evaluatedConst);
	}

}
