/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms.model;

public interface Effect {
    void applyEffect(PlayArea visitor);

    String getDescription();
}
