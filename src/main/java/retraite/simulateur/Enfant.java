package retraite.simulateur;

public class Enfant {
    private boolean educationEnfant;
    private boolean educationPartageeEnfant;
    private boolean materniteEnfant;
    // Nom Prenom Age ?


    public boolean isEducationEnfant() {
        return educationEnfant;
    }

    public void setEducationEnfant(boolean educationEnfant) {
        this.educationEnfant = educationEnfant;
    }

    public boolean isEducationPartageeEnfant() {
        return educationPartageeEnfant;
    }

    public void setEducationPartageeEnfant(boolean educationPartageeEnfant) {
        this.educationPartageeEnfant = educationPartageeEnfant;
    }

    public boolean isMaterniteEnfant() {
        return materniteEnfant;
    }

    public void setMaterniteEnfant(boolean materniteEnfant) {
        this.materniteEnfant = materniteEnfant;
    }
}