public abstract class AbstractCompatibilityHandler implements CompatibilityHandler {
    private CompatibilityHandler nextHandler;

    @Override
    public CompatibilityHandler setNext(CompatibilityHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    protected RecipientEvaluation continueChain(
            DonorOrgan organ,
            Recipient recipient,
            RecipientEvaluation evaluation
    ) {
        if (nextHandler == null || !evaluation.isEligible()) {
            return evaluation;
        }
        return nextHandler.handle(organ, recipient, evaluation);
    }
}
