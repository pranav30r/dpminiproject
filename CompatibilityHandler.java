public interface CompatibilityHandler {
    CompatibilityHandler setNext(CompatibilityHandler nextHandler);

    RecipientEvaluation handle(DonorOrgan organ, Recipient recipient, RecipientEvaluation evaluation);
}
