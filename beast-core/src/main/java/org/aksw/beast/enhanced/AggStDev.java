package org.aksw.beast.enhanced;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.expr.aggregate.Accumulator;
import org.apache.jena.sparql.expr.aggregate.AccumulatorExpr;
import org.apache.jena.sparql.expr.aggregate.Aggregator;
import org.apache.jena.sparql.expr.aggregate.AggregatorBase;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.function.FunctionEnv;

/**
 * This class will be removed soon, as Jena's AccStatBase classes can now be
 * used with RdfGroupBy.
 *
 *
 * In Jena's code there is also a reference on how to compute stDev with a single pass:
 * https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
 *
 * @author Claus Stadler
 *
 */
public class AggStDev extends AggregatorBase {
    private static final NodeValue noValuesToAvg;

    public AggStDev(Expr expr) {
        super("STDEV", false, expr);
    }

    public Aggregator copy(ExprList expr) {
        return new AggStDev(expr.get(0));
    }

    public Accumulator createAccumulator() {
        return new AggStDev.AccStDev(this.getExpr());
    }

    public Node getValueEmpty() {
        return NodeValue.toNode(noValuesToAvg);
    }

    public int hashCode() {
        return 3135 ^ this.getExprList().hashCode();
    }

    public boolean equals(Aggregator other, boolean bySyntax) {
        if (other == null) {
            return false;
        } else if (this == other) {
            return true;
        } else if (!(other instanceof AggStDev)) {
            return false;
        } else {
            AggStDev a = (AggStDev) other;
            return this.exprList.equals(a.exprList, bySyntax);
        }
    }

    static {
        noValuesToAvg = NodeValue.nvZERO;
    }

    private static class AccStDev extends AccumulatorExpr {
        private NodeValue total;
        private int count;
        private List<NodeValue> values = new ArrayList<>();
        static final boolean DEBUG = false;

        public AccStDev(Expr expr) {
            super(expr, false);
            this.total = AggStDev.noValuesToAvg;
            this.count = 0;
        }

        protected void accumulate(NodeValue nv, Binding binding,
                FunctionEnv functionEnv) {
            if (nv.isNumber()) {
                ++this.count;
                if (this.total == AggStDev.noValuesToAvg) {
                    this.total = nv;
                } else {
                    this.total = XSDFuncOp.numAdd(nv, this.total);
                }

                values.add(nv);

            } else {
                throw new ExprEvalException("avg: not a number: " + nv);
            }
        }

        protected void accumulateError(Binding binding,
                FunctionEnv functionEnv) {
        }

        public NodeValue getAccValue() {
            if (this.count == 0) {
                return AggStDev.noValuesToAvg;
            } else if (super.errorCount != 0L) {
                return null;
            } else {
                NodeValue nvCount = NodeValue.makeInteger((long) this.count);
                NodeValue avg = XSDFuncOp.numDivide(this.total, nvCount);

                NodeValue stDev = values.stream()
                    .map(x -> XSDFuncOp.numSubtract(x, avg))
                    .map(x -> XSDFuncOp.numMultiply(x, x))
                    .reduce(XSDFuncOp::numAdd)
                    .map(x -> XSDFuncOp.numDivide(x, nvCount))
                    .map(XSDFuncOp::sqrt)
                    .orElse(noValuesToAvg)
                    ;

                return stDev;
            }
        }
    }
}