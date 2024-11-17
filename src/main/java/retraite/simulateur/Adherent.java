package retraite.simulateur;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class Adherent {
    private Date dateNaissance;
    private String nom;
    private String prenom;
    private int sexe;
    private int ageDepMois;
    private int nbEnfants;
    private Date dateRetraiteSouhait = new Date();
    @JsonProperty("SAM")
    private int SAM;
    private Boolean trimestrePartage;
    private int nbEnfantsPartages;
    private int trimValide = 0;
    private int trimRequis = 0;
    private int trimHandicap = 0;
    private int methodeTaux = 1;
    private Date dateSimulation = new Date();
    private List<Enfant> enfants;
    @JsonProperty("carriereLongue")
    private String carriereLongue;

    public String getCarriereLongue() {
        return carriereLongue;
    }

    public void setCarriereLongue(String carriereLongue) {
        this.carriereLongue = carriereLongue;
    }

    public int getMethodeTaux() {
        return methodeTaux;
    }

    public void setMethodeTaux(int methodeTaux) {
        this.methodeTaux = methodeTaux;
    }

    public Date getDateSimulation() {
        return dateSimulation;
    }

    public void setDateSimulation(Date dateSimulation) {
        this.dateSimulation = dateSimulation;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getSexe() {
        return sexe;
    }

    public void setSexe(int sexe) {
        this.sexe = sexe;
    }

    public int getAgeDepMois() {
        return ageDepMois;
    }

    public void setAgeDepMois(int ageDepMois) {
        this.ageDepMois = ageDepMois;
    }

    public int getNbEnfants() {
        return nbEnfants;
    }

    public void setNbEnfants(int nbEnfants) {
        this.nbEnfants = nbEnfants;
    }

    public Date getDateRetraiteSouhait() {
        return dateRetraiteSouhait;
    }

    public void setDateRetraiteSouhait(Date dateRetraiteSouhait) {
        this.dateRetraiteSouhait = dateRetraiteSouhait;
    }

    public int getSAM() {
        return SAM;
    }

    public void setSAM(int SAM) {
        this.SAM = SAM;
    }

    public Boolean getTrimestrePartage() {
        return trimestrePartage;
    }

    public void setTrimestrePartage(Boolean trimestrePartage) {
        this.trimestrePartage = trimestrePartage;
    }

    public int getNbEnfantsPartages() {
        return nbEnfantsPartages;
    }

    public void setNbEnfantsPartages(int nbEnfantsPartages) {
        this.nbEnfantsPartages = nbEnfantsPartages;
    }

    public int getTrimValide() {
        return trimValide;
    }

    public void setTrimValide(int trimValide) {
        this.trimValide = trimValide;
    }

    public int getTrimRequis() {
        return trimRequis;
    }

    public void setTrimRequis(int trimRequis) {
        this.trimRequis = trimRequis;
    }

    public int getTrimHandicap() {
        return trimHandicap;
    }

    public void setTrimHandicap(int trimHandicap) {
        this.trimHandicap = trimHandicap;
    }

    public List<Enfant> getEnfants() {
        return enfants;
    }

    public void setEnfants(List<Enfant> enfants) {
        this.enfants = enfants;
    }
}
