package retraite.simulateur;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

@Service
public class ServiceRetraite {
// DONE max 1 pour la fraction des trimestres
// DONE décote du taux
// DONE Rajouter date de retraite souhaitée
// DONE Si vous avez eu au moins 3 enfants, le montant de votre pension de retraite de l'Assurance retraite est majoré de 10%
    // DONE surcote (trimestres travaillés après l'age de départ à la retraite)
    // FIXME Il n'y a pour l'instant pas de vérification de quels trimestres ont été cotisés après l'âge de départ à la retraite
// DONE decote pour partir avant l'age de retraite


    // TODO taux plein automatique à 67 ans (cocher une case qui enclenche ce mode de calcul différent)
    // TODO handicapés
    // TODO carriere longue ()
    // TODO gérer exceptions et valeurs manquantes
    // TODO enfants (pour chaque enfant, savoir maternité et éducation)
    // TODO pouvoir choisir la data à laquelle est faite la simulation
    public double calculerEpargneRetraite(Adherent adherent) {
        int nbTrimestresManquants = calculerTrimestresManquants(adherent);
        double taux = calculerTaux(nbTrimestresManquants);
        double fractionTrim = calculerFractionTrimestres(adherent, nbTrimestresManquants);

        // Calculer la décote pour départ avant l'âge légal
        double decoteAge = calculerDecotePourDepartAnticipe(
                adherent.getDateNaissance(),
                adherent.getDateRetraiteSouhait()
        );

        // Calcul de la pension brute
        double epargneBrute = adherent.getSAM() * taux * fractionTrim * decoteAge;

        // Ajouter la surcote si applicable
        double surcote = calculerSurcote(adherent);
        epargneBrute *= surcote;

        // Ajouter la majoration pour les enfants si applicable
        if (adherent.getNbEnfants() >= 3) {
            epargneBrute *= 1.10; // Majoration de 10% pour 3 enfants ou plus
        }

        // Arrondir le montant final
        BigDecimal epargneArrondie = BigDecimal.valueOf(epargneBrute).setScale(2, RoundingMode.HALF_UP);
        System.out.println(epargneArrondie);
        return epargneArrondie.doubleValue();
    }

    public int calculerTrimestresRequis(Date dateNaissance) {
        // Convertir Date en LocalDate pour faciliter les comparaisons
        LocalDate dateNaissanceLocal = new java.sql.Date(dateNaissance.getTime()).toLocalDate();

        if (dateNaissanceLocal.isBefore(LocalDate.of(1961, 1, 1))) {
            return 167; // 1960 et avant
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1961, 9, 1))) {
            return 168; // 1er janvier au 31 août 1961
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1962, 1, 1))) {
            return 169; // 1er septembre au 31 décembre 1961
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 1, 1))) {
            return 169; // 1962
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1964, 1, 1))) {
            return 170; // 1963
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1965, 1, 1))) {
            return 171; // 1964
        } else {
            return 172; // 1965 et au-delà
        }
    }

    public LocalDate calculerAgeDepart(Date dateNaissance) {
        LocalDate dateNaissanceLocal = new java.sql.Date(dateNaissance.getTime()).toLocalDate();

        if (dateNaissanceLocal.isBefore(LocalDate.of(1958, 1, 1))) {
            return dateNaissanceLocal.plusYears(62); // 62 ans pour 1955 - 1957
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1961, 1, 1))) {
            return dateNaissanceLocal.plusYears(62); // 62 ans pour 1958 - 1960
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1961, 9, 1))) {
            return dateNaissanceLocal.plusYears(62); // 62 ans pour 1er janvier au 31 août 1961
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1962, 1, 1))) {
            return dateNaissanceLocal.plusYears(62).plusMonths(3); // 62 ans et 3 mois pour 1er septembre - 31 décembre 1961
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 1, 1))) {
            return dateNaissanceLocal.plusYears(62).plusMonths(6); // 62 ans et 6 mois pour 1962
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1964, 1, 1))) {
            return dateNaissanceLocal.plusYears(62).plusMonths(9); // 62 ans et 9 mois pour 1963
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1965, 1, 1))) {
            return dateNaissanceLocal.plusYears(63); // 63 ans pour 1964
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1966, 1, 1))) {
            return dateNaissanceLocal.plusYears(63).plusMonths(3); // 63 ans et 3 mois pour 1965
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1967, 1, 1))) {
            return dateNaissanceLocal.plusYears(63).plusMonths(6); // 63 ans et 6 mois pour 1966
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1968, 1, 1))) {
            return dateNaissanceLocal.plusYears(63).plusMonths(9); // 63 ans et 9 mois pour 1967
        } else {
            return dateNaissanceLocal.plusYears(64); // 64 ans pour 1968 et après
        }
    }

    public int calculerTrimestresEntreDates(Date dateDebut, Date dateFin) {
        // Convertir les dates en LocalDate
        LocalDate debut = new java.sql.Date(dateDebut.getTime()).toLocalDate();
        LocalDate fin = new java.sql.Date(dateFin.getTime()).toLocalDate();

        // Vérifier que la date de fin est postérieure à la date de début
        if (!fin.isAfter(debut)) {
            return 0;
        }
        // Calculer les trimestres civils entre les deux dates
        int trimestres = 0;
        // Parcourir les mois entre les deux dates
        while (debut.isBefore(fin)) {
            int mois = debut.getMonthValue();
            // Si le mois est le dernier d'un trimestre civil, ajouter un trimestre
            if (mois == 3 || mois == 6 || mois == 9 || mois == 12) {
                trimestres++;
            }
            // Passer au mois suivant
            debut = debut.plusMonths(1);
        }
        return trimestres;
    }

    public int calculerTrimestresManquants(Adherent adherent) {
        // Calcul des trimestres requis à partir de la date de naissance
        int trimestresRequis = calculerTrimestresRequis(adherent.getDateNaissance());

        // Ajouter les trimestres entre aujourd'hui et la date de retraite souhaitée si renseignée
        int nbTrimValide = adherent.getTrimValide();
        if (adherent.getDateRetraiteSouhait() != null) {
            int trimestresSup = calculerTrimestresEntreDates(new Date(), adherent.getDateRetraiteSouhait());
            nbTrimValide += trimestresSup;
        }

        // Ajouter les trimestres pour les enfants (supposons qu'une méthode existe pour ça)
        nbTrimValide += calculerTrimestresParEnfant(adherent);

        // Ajouter d'autres critères spécifiques si nécessaire (par exemple, trimestres de handicap)
        // nbTrimValide += adherent.getTrimHandicap();

        // Calcul final des trimestres manquants
        return Math.max(trimestresRequis - nbTrimValide, 0);
    }


    public double calculerTaux(int nbTrimestresManquants) {
        double tauxPlein = 0.5; // Taux plein à 50%
        double tauxDecoteParTrimestre = 0.625; // 0.625 de décote du taux par trimestre manquant
        double tauxMinimum = 0.375; // Taux minimum fixé à 37,5%
        int nbTrimestresManquantsMax = 20;
        nbTrimestresManquants = Math.min(nbTrimestresManquantsMax, nbTrimestresManquants);

        // Calcul de la réduction totale en fonction des trimestres manquants
        double taux = tauxPlein - ((tauxDecoteParTrimestre * nbTrimestresManquants)/100);

        // S'assurer que le taux ne descend pas en dessous du taux minimum
        return Math.max(taux, tauxMinimum);
    }

    public double calculerFractionTrimestres(Adherent adherent, int nbTrimestresManquants){
        int nbTrimestresRequis = calculerTrimestresRequis(adherent.getDateNaissance());
        int nbTrimestresValides = nbTrimestresRequis - nbTrimestresManquants; // S'adapte selon la date de départ choisie et les enfants
        double fraction = (double) nbTrimestresValides / nbTrimestresRequis;
        fraction = Math.min(1, fraction);
        return fraction;
    }

    public double calculerSurcote(Adherent adherent) {
        // Âge légal et trimestres requis
        LocalDate ageLegal = calculerAgeDepart(adherent.getDateNaissance());
        int trimestresRequis = calculerTrimestresRequis(adherent.getDateNaissance());

        // Convertir la date de naissance en LocalDate
        LocalDate dateNaissanceLocal = new java.sql.Date(adherent.getDateNaissance().getTime()).toLocalDate();
        LocalDate dateRetraiteSouhaitee = new java.sql.Date(adherent.getDateRetraiteSouhait().getTime()).toLocalDate();

        // Vérifier si la personne a atteint l'âge légal et le taux plein
        if (!dateRetraiteSouhaitee.isAfter(ageLegal)) {
            return 0; // Pas de surcote si la date de départ est avant l'âge légal
        }

        int nbTrimestresValides = adherent.getTrimValide();
        if (nbTrimestresValides < trimestresRequis) {
            return 0; // Pas de surcote si le taux plein n'est pas atteint
        }

        // Calculer les trimestres supplémentaires travaillés après le taux plein
        int trimestresSup = calculerTrimestresEntreDates(java.sql.Date.valueOf(ageLegal), adherent.getDateRetraiteSouhait());
        double surcote = trimestresSup * 0.0125; // 1,25% par trimestre supplémentaire

        return 1 + surcote;
    }

    public double calculerDecoteAge(Date dateNaissance, Date dateRetraiteSouhaitee) {
        LocalDate ageLegal = calculerAgeDepart(dateNaissance); // Âge légal calculé
        LocalDate ageSouhaite = new java.sql.Date(dateRetraiteSouhaitee.getTime()).toLocalDate();
        // Calculer la différence en années et mois
        Period diff = Period.between(ageSouhaite, ageLegal);
        int trimestresAvantAgeLegal = (diff.getYears() * 12 + diff.getMonths()) / 3; // Convertir en trimestres
        // Décote par trimestre manquant (1,25% par trimestre, soit 0.0125)
        double tauxDecoteParTrimestre = 0.0125;
        return Math.min(trimestresAvantAgeLegal * tauxDecoteParTrimestre, 1); // Limiter à une décote maximale de 100%
    }

    public double calculerDecotePourDepartAnticipe(Date dateNaissance, Date dateRetraiteSouhaitee) {
        LocalDate ageLegal = calculerAgeDepart(dateNaissance); // Âge légal calculé
        LocalDate dateSouhaitee = new java.sql.Date(dateRetraiteSouhaitee.getTime()).toLocalDate();

        if (!dateSouhaitee.isBefore(ageLegal)) {
            return 1.0; // Pas de décote si la date de départ est à l'âge légal ou après
        }

        // Calculer la différence en trimestres avant l'âge légal
        Period diff = Period.between(dateSouhaitee, ageLegal);
        int trimestresAvantAgeLegal = Math.abs((diff.getYears() * 12 + diff.getMonths()) / 3); // Convertir en trimestres positifs

        // Décote par trimestre manquant (1,25% par trimestre)
        double tauxDecoteParTrimestre = 0.0125;
        double decote = 1 - (trimestresAvantAgeLegal * tauxDecoteParTrimestre);

        return Math.max(decote, 0.0); // S'assurer que la décote ne tombe pas en dessous de 0
    }

    public int calculerTrimestresParEnfant(Adherent adherent){
        int nbTrimestresEnfants=0;
        if (adherent.getSexe()==1){ // Est un homme
            // + 2 pour l'éducation
            nbTrimestresEnfants += adherent.getNbEnfants()*2;
        } else { // Est une femme
            // + 4 pour l'éducation
            nbTrimestresEnfants += adherent.getNbEnfants()*4; // FIXME Ajouter des options pour le cas ou les trimestres sont partagés avec le père
            // + 4 pour la maternité
            nbTrimestresEnfants += adherent.getNbEnfants()*4;
        }
        return nbTrimestresEnfants;
    }
}
