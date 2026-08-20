public enum UrgencyLevel {
    LOW(5),
    MEDIUM(10),
    HIGH(15),
    CRITICAL(25);

    private final int scoreContribution;

    UrgencyLevel(int scoreContribution) {
        this.scoreContribution = scoreContribution;
    }

    public int getScoreContribution() {
        return scoreContribution;
    }
}
