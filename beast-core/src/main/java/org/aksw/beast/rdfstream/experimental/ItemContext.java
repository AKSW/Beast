package org.aksw.beast.rdfstream.experimental;

/**
 * Context for an item in a workflow.
 *
 * @author raven
 *
 * @param <I>
 * @param <J>
 * @param <S>
 */
public class ItemContext<I, S> {
    protected I item;
    protected S stepExecCtx;

    public ItemContext(I item, S stepExecCtx) {
        this.item = item;
        this.stepExecCtx = stepExecCtx;
    }

    public I getItem() {
        return item;
    }

    public S getStepExecCtx() {
        return stepExecCtx;
    }
}
