/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

public interface Effect {
    void applyEffect(PlayArea visitor);

    String getDescription();

    class DamageEffect implements Effect {
        private final int damageAmount;
        private final String description;

        public DamageEffect(int damageAmount, String description) {
            this.damageAmount = damageAmount;
            this.description = description;
        }

        public void applyEffect(PlayArea playArea) {
            // implementation details
        }

        public String getDescription() {
            return description;
        }
    }
}
