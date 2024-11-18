package retraite.simulateur;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.List;

@Service
public class ServiceRetraite {
// DONE max 1 pour la fraction des trimestres
// DONE décote du taux
// DONE Rajouter date de retraite souhaitée
// DONE Si vous avez eu au moins 3 enfants, le montant de votre pension de retraite de l'Assurance retraite est majoré de 10%
    // DONE surcote (trimestres travaillés après l'age de départ à la retraite)
    // FIXME Il n'y a pour l'instant pas de vérification de quels trimestres ont été cotisés après l'âge de départ à la retraite
// DONE decote pour partir avant l'age de retraite
// DONE taux plein automatique à 67 ans (cocher une case qui enclenche ce mode de calcul différent)
    // DONE enfants (pour chaque enfant, savoir maternité et éducation)
    // DONE carriere longue

    // TODO handicapés
    // Age de départ, nombre de trimestre, taux plein direct

    // TODO gérer exceptions et valeurs manquantes

    // TODO pouvoir choisir la date à laquelle est faite la simulation
    // TODO envoyer un message si la personne ne peut pas légalement prendre sa retraite
    public double calculerEpargneRetraite(Adherent adherent) {
        int nbTrimestresManquants = calculerTrimestresManquants(adherent);
        double taux = calculerTaux(adherent, nbTrimestresManquants);
        double fractionTrim = calculerFractionTrimestres(adherent, nbTrimestresManquants);

        // Calculer la décote pour départ avant l'âge légal
        double decoteAge = calculerDecotePourDepartAnticipe(
                adherent.getDateNaissance(),
                adherent.getDateRetraiteSouhait(),
                adherent.getCarriereLongue(),
                adherent.getTrimHandicap()
        );

        // Calcul de la pension brute
        double epargneBrute = adherent.getSAM() * taux * fractionTrim * decoteAge;

        // Ajouter la surcote si applicable
        //double surcote = calculerSurcote(adherent);
        //epargneBrute *= surcote;

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

        if (dateNaissanceLocal.isBefore(LocalDate.of(1960, 1, 1))) {
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

    public LocalDate calculerAgeDepart(Date dateNaissance, String carriereLongue) {
        LocalDate dateNaissanceLocal = new java.sql.Date(dateNaissance.getTime()).toLocalDate();
        LocalDate ageDepartCarriereLongue = null;

        if ("21".equals(carriereLongue)) {
            ageDepartCarriereLongue = dateNaissanceLocal.plusYears(63);
        } else if ("20".equals(carriereLongue)) {
            if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 8, 31))) {
                ageDepartCarriereLongue = dateNaissanceLocal.plusYears(60);
            } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 12, 31))) {
                ageDepartCarriereLongue = dateNaissanceLocal.plusYears(60).plusMonths(3);
            } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 12, 31))) {
                ageDepartCarriereLongue = dateNaissanceLocal.plusYears(60).plusMonths(6);
            } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 12, 31))) {
                ageDepartCarriereLongue = dateNaissanceLocal.plusYears(60).plusMonths(9);
            } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 12, 31))) {
                ageDepartCarriereLongue = dateNaissanceLocal.plusYears(61);
            } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 12, 31))) {
                ageDepartCarriereLongue = dateNaissanceLocal.plusYears(60).plusMonths(9);
            } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 12, 31))) {
                ageDepartCarriereLongue = dateNaissanceLocal.plusYears(61);
            } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 12, 31))) {
                ageDepartCarriereLongue = dateNaissanceLocal.plusYears(61).plusMonths(3);
            } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 12, 31))) {
                ageDepartCarriereLongue = dateNaissanceLocal.plusYears(61).plusMonths(6);
            } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 12, 31))) {
                ageDepartCarriereLongue = dateNaissanceLocal.plusYears(61).plusMonths(9);
            } else {
                ageDepartCarriereLongue = dateNaissanceLocal.plusYears(62);
            }
        } else if ("18".equals(carriereLongue)) {
            ageDepartCarriereLongue = dateNaissanceLocal.plusYears(60);
        } else if ("16".equals(carriereLongue)) {
            ageDepartCarriereLongue = dateNaissanceLocal.plusYears(58);
        }

        LocalDate ageDepart;
        if (dateNaissanceLocal.isBefore(LocalDate.of(1958, 1, 1))) {
            ageDepart = dateNaissanceLocal.plusYears(62); // 62 ans pour 1955 - 1957
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1961, 1, 1))) {
            ageDepart = dateNaissanceLocal.plusYears(62); // 62 ans pour 1958 - 1960
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1961, 9, 1))) {
            ageDepart = dateNaissanceLocal.plusYears(62); // 62 ans pour 1er janvier au 31 août 1961
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1962, 1, 1))) {
            ageDepart = dateNaissanceLocal.plusYears(62).plusMonths(3); // 62 ans et 3 mois pour 1er septembre - 31 décembre 1961
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1963, 1, 1))) {
            ageDepart = dateNaissanceLocal.plusYears(62).plusMonths(6); // 62 ans et 6 mois pour 1962
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1964, 1, 1))) {
            ageDepart = dateNaissanceLocal.plusYears(62).plusMonths(9); // 62 ans et 9 mois pour 1963
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1965, 1, 1))) {
            ageDepart = dateNaissanceLocal.plusYears(63); // 63 ans pour 1964
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1966, 1, 1))) {
            ageDepart = dateNaissanceLocal.plusYears(63).plusMonths(3); // 63 ans et 3 mois pour 1965
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1967, 1, 1))) {
            ageDepart = dateNaissanceLocal.plusYears(63).plusMonths(6); // 63 ans et 6 mois pour 1966
        } else if (dateNaissanceLocal.isBefore(LocalDate.of(1968, 1, 1))) {
            ageDepart = dateNaissanceLocal.plusYears(63).plusMonths(9); // 63 ans et 9 mois pour 1967
        } else {
            ageDepart = dateNaissanceLocal.plusYears(64); // 64 ans pour 1968 et après
        }
        // Retourner la plus petite des deux valeurs
        if (ageDepartCarriereLongue != null && ageDepartCarriereLongue.isBefore(ageDepart)) {
            return ageDepartCarriereLongue;
        }
        return ageDepart;
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
        int trimestresRequis;
        // Calcul des trimestres requis à partir de la date de naissance
        if (adherent.getTrimHandicap() > 0) {
            trimestresRequis = calculerTrimestresRequisHandicap(adherent.getDateNaissance(), adherent.getDateRetraiteSouhait());
        } else {
            trimestresRequis = calculerTrimestresRequis(adherent.getDateNaissance());
        }

        // Ajouter les trimestres entre aujourd'hui et la date de retraite souhaitée si renseignée
        int nbTrimValide = adherent.getTrimValide() + adherent.getTrimHandicap();
        // FIXME refaire la surcote des trimestres supplémentaires
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

    private double calculerTauxClassique(int nbTrimestresManquants, double tauxPlein, double tauxDecoteParTrimestre, double tauxMinimum) {
        double taux = tauxPlein - (nbTrimestresManquants * tauxDecoteParTrimestre);
        return Math.max(taux, tauxMinimum); // S'assurer que le taux ne descend pas sous le minimum
    }

    private double calculerTauxAutomatique(Adherent adherent, double tauxPlein, double tauxDecoteParTrimestre, double tauxMinimum) {
        LocalDate dateNaissance = new java.sql.Date(adherent.getDateNaissance().getTime()).toLocalDate();
        LocalDate dateRetraiteSouhaitee = new java.sql.Date(adherent.getDateRetraiteSouhait().getTime()).toLocalDate();

        // Calcul des trimestres manquants pour atteindre 67 ans
        LocalDate dateAtteinte67Ans = dateNaissance.plusYears(67);
        int trimestresManquants = calculerTrimestresEntreDates(
                java.sql.Date.valueOf(dateRetraiteSouhaitee),
                java.sql.Date.valueOf(dateAtteinte67Ans)
        );

        // Décote pour les trimestres manquants
        double taux = tauxPlein - (trimestresManquants * tauxDecoteParTrimestre);

        // S'assurer que le taux ne descend pas en dessous de 0
        return Math.max(taux, tauxMinimum);
    }

    public double calculerTaux(Adherent adherent, int nbTrimestresManquantsPourDecote) {
        double tauxPlein = 0.5; // Taux plein à 50%
        double tauxDecoteParTrimestre = 0.00625; // Décote de 0.625 % par trimestre manquant
        double tauxMinimum = 0.375; // Taux minimum de 37.5 %
        int nbTrimestresManquantsMax = 20; // Limite à 20 trimestres pour la décote
        nbTrimestresManquantsPourDecote = Math.min(nbTrimestresManquantsMax, nbTrimestresManquantsPourDecote);
        double taux;
        if (adherent.getTrimHandicap() > 0) {
            taux = tauxPlein;
        } else {
            switch (adherent.getMethodeTaux()) {
                case 2: // Taux plein classique
                    taux = calculerTauxClassique(nbTrimestresManquantsPourDecote, tauxPlein, tauxDecoteParTrimestre, tauxMinimum);
                    break;

                case 3: // Taux plein automatique
                    taux = calculerTauxAutomatique(adherent, tauxPlein, tauxDecoteParTrimestre, tauxMinimum);
                    break;

                default: // La plus avantageuse
                    taux = Math.max(
                            calculerTauxClassique(nbTrimestresManquantsPourDecote, tauxPlein, tauxDecoteParTrimestre, tauxMinimum),
                            calculerTauxAutomatique(adherent, tauxPlein, tauxDecoteParTrimestre, tauxMinimum)
                    );
            }
        }

        return taux;
    }

    public double calculerFractionTrimestres(Adherent adherent, int nbTrimestresManquants) {
        int nbTrimestresRequis = 0;
        if (adherent.getTrimHandicap()>0){
            nbTrimestresRequis = calculerTrimestresRequisHandicap(adherent.getDateNaissance(), adherent.getDateRetraiteSouhait());
        } else {
            nbTrimestresRequis = calculerTrimestresRequis(adherent.getDateNaissance());
        }
        int nbTrimestresValides = nbTrimestresRequis - nbTrimestresManquants; // S'adapte selon la date de départ choisie et les enfants
        double fraction = (double) nbTrimestresValides / nbTrimestresRequis;
        fraction = Math.min(1, fraction);
        return fraction;
    }

    public double calculerSurcote(Adherent adherent) {
        LocalDate ageLegal;
        int trimestresRequis=0;

        // Âge légal et trimestres requis
        if(adherent.getTrimHandicap()>0){
            ageLegal = calculerAgeDepartHandicap(adherent.getDateNaissance(), adherent.getTrimHandicap()+adherent.getTrimValide());
            trimestresRequis = calculerTrimestresRequisHandicap(adherent.getDateNaissance(), adherent.getDateRetraiteSouhait());
        } else {
            ageLegal = calculerAgeDepart(adherent.getDateNaissance(), adherent.getCarriereLongue());
            trimestresRequis = calculerTrimestresRequis(adherent.getDateNaissance());
        }

        // Convertir la date de naissance en LocalDate
        LocalDate dateNaissanceLocal = new java.sql.Date(adherent.getDateNaissance().getTime()).toLocalDate();
        LocalDate dateRetraiteSouhaitee = new java.sql.Date(adherent.getDateRetraiteSouhait().getTime()).toLocalDate();

        // Vérifier si la personne a atteint l'âge légal et le taux plein
        if (!dateRetraiteSouhaitee.isAfter(ageLegal)) {
            return 1; // Pas de surcote si la date de départ est avant l'âge légal
        }

        int nbTrimestresValides = adherent.getTrimValide(); // FIXME le taux plein peut etre atteint avec les trims enfants et autres
        if (nbTrimestresValides < trimestresRequis) {
            return 1; // Pas de surcote si le taux plein n'est pas atteint
        }

        // Calculer les trimestres supplémentaires travaillés après le taux plein
        int trimestresSup = calculerTrimestresEntreDates(java.sql.Date.valueOf(ageLegal), adherent.getDateRetraiteSouhait());
        double surcote = trimestresSup * 0.0125; // 1,25% par trimestre supplémentaire

        return 1 + surcote;
    }


    public double calculerDecotePourDepartAnticipe(Date dateNaissance, Date dateRetraiteSouhaitee, String carriereLongue, int trimHandicap) {
        // TODO if handicap return 1.0
        if(trimHandicap>0){
            return  1.0;
        }
        LocalDate ageLegal = calculerAgeDepart(dateNaissance, carriereLongue); // Âge légal calculé
        LocalDate dateSouhaitee = new java.sql.Date(dateRetraiteSouhaitee.getTime()).toLocalDate();

        if (!dateSouhaitee.isBefore(ageLegal)) {
            return 1.0; // Pas de décote si la date de départ est à l'âge légal ou après
        }

        // Calculer la différence en trimestres avant l'âge légal
        Period diff = Period.between(dateSouhaitee, ageLegal);
        int trimestresAvantAgeLegal = Math.abs((diff.getYears() * 12 + diff.getMonths()) / 3); // Convertir en trimestres positifs

        // Décote par trimestre manquant (1,25% par trimestre)
        double tauxDecoteParTrimestre = 0.0125;
        double decote = 1.0 - (trimestresAvantAgeLegal * tauxDecoteParTrimestre);

        return Math.max(decote, 0.0); // S'assurer que la décote ne tombe pas en dessous de 0
    }

    public int calculerTrimestresParEnfant(Adherent adherent) {
        int nbTrimestresEnfants = 0;
        List<Enfant> enfants = adherent.getEnfants();
        if (adherent.getSexe() == 1) { // Est un homme
            for (Enfant e : enfants) {
                if (e.isEducationPartageeEnfant()) {
                    nbTrimestresEnfants += 2;
                }
            }
        } else { // Est une femme
            for (Enfant e : enfants) {
                if (e.isMaterniteEnfant()) {
                    nbTrimestresEnfants += 4;
                }
                if (e.isEducationPartageeEnfant()) {
                    nbTrimestresEnfants += 2;
                } else if (e.isEducationEnfant()) {
                    nbTrimestresEnfants += 4;
                }
            }
        }
        return nbTrimestresEnfants;
    }

    public double calculerEpargneRetraiteHandicap(Adherent adherent) {
        int nbTrimestresManquants = calculerTrimestresManquants(adherent);
        double taux = calculerTaux(adherent, nbTrimestresManquants);
        double fractionTrim = calculerFractionTrimestres(adherent, nbTrimestresManquants);

        // Calculer la décote pour départ avant l'âge légal
        double decoteAge = calculerDecotePourDepartAnticipe(
                adherent.getDateNaissance(),
                adherent.getDateRetraiteSouhait(),
                adherent.getCarriereLongue(),
                adherent.getTrimHandicap()
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

    public int calculerTrimestresRequisHandicap(Date dateNaissance, Date dateRetraiteSouhaitee) {
        // Convertir Date en LocalDate pour faciliter les calculs
        LocalDate dateNaissanceLocal = new java.sql.Date(dateNaissance.getTime()).toLocalDate();
        LocalDate dateRetraiteLocal = new java.sql.Date(dateRetraiteSouhaitee.getTime()).toLocalDate();

        // Calculer l'âge de départ à la retraite
        int ageDepart = Period.between(dateNaissanceLocal, dateRetraiteLocal).getYears();

        // Extraire l'année de naissance
        int anneeNaissance = dateNaissanceLocal.getYear();

        // Déterminer les trimestres requis en fonction de l'année de naissance et de l'âge de départ
        if (anneeNaissance < 1961 || (anneeNaissance == 1961 && dateNaissanceLocal.getMonthValue() < 9)) {
            return 88; // Avant le 1er septembre 1961
        } else if (anneeNaissance == 1961 || anneeNaissance == 1962) {
            return 68; // Entre le 1er septembre 1961 et 31 décembre 1962
        } else if (anneeNaissance == 1963) {
            return 68;
        } else if (anneeNaissance == 1964) {
            if (ageDepart == 58) return 79;
            return 69;
        } else if (anneeNaissance == 1965) {
            if (ageDepart == 57) return 89;
            if (ageDepart == 58) return 79;
            return 69;
        } else if (anneeNaissance == 1966) {
            if (ageDepart == 56) return 99;
            if (ageDepart == 57) return 89;
            if (ageDepart == 58) return 79;
            return 69;
        } else if (anneeNaissance >= 1967 && anneeNaissance <= 1969) {
            if (ageDepart == 55) return 110;
            if (ageDepart == 56) return 100;
            if (ageDepart == 57) return 90;
            if (ageDepart == 58) return 80;
            return 70;
        } else if (anneeNaissance >= 1970 && anneeNaissance <= 1972) {
            if (ageDepart == 55) return 111;
            if (ageDepart == 56) return 101;
            if (ageDepart == 57) return 91;
            if (ageDepart == 58) return 81;
            return 71;
        } else {
            if (ageDepart == 55) return 112;
            if (ageDepart == 56) return 102;
            if (ageDepart == 57) return 92;
            if (ageDepart == 58) return 82;
            return 72;
        }

        // Si aucun cas ne correspond
        //throw new IllegalArgumentException("Combinaison année de naissance et âge de départ invalide.");
    }

    public LocalDate calculerAgeDepartHandicap(Date dateNaissance, int trimestresCotises) {
        LocalDate dateNaissanceLocal = new java.sql.Date(dateNaissance.getTime()).toLocalDate();
        int anneeNaissance = dateNaissanceLocal.getYear();

        // Vérification des conditions en fonction de l'année de naissance et des trimestres cotisés
        if (anneeNaissance < 1961 || (anneeNaissance == 1961 && dateNaissanceLocal.getMonthValue() < 9)) {
            if (trimestresCotises >= 68) {
                return dateNaissanceLocal.plusYears(59);
            }
        } else if (anneeNaissance <= 1963) {
            if (trimestresCotises >= 68) {
                return dateNaissanceLocal.plusYears(59);
            }
        } else if (anneeNaissance == 1964) {
            if (trimestresCotises >= 79) {
                return dateNaissanceLocal.plusYears(58);
            } else if (trimestresCotises >= 69) {
                return dateNaissanceLocal.plusYears(59);
            }
        } else if (anneeNaissance == 1965) {
            if (trimestresCotises >= 89) {
                return dateNaissanceLocal.plusYears(57);
            } else if (trimestresCotises >= 79) {
                return dateNaissanceLocal.plusYears(58);
            } else if (trimestresCotises >= 69) {
                return dateNaissanceLocal.plusYears(59);
            }
        } else if (anneeNaissance == 1966) {
            if (trimestresCotises >= 99) {
                return dateNaissanceLocal.plusYears(56);
            } else if (trimestresCotises >= 89) {
                return dateNaissanceLocal.plusYears(57);
            } else if (trimestresCotises >= 79) {
                return dateNaissanceLocal.plusYears(58);
            } else if (trimestresCotises >= 69) {
                return dateNaissanceLocal.plusYears(59);
            }
        } else if (anneeNaissance >= 1967 && anneeNaissance <= 1969) {
            if (trimestresCotises >= 110) {
                return dateNaissanceLocal.plusYears(55);
            } else if (trimestresCotises >= 100) {
                return dateNaissanceLocal.plusYears(56);
            } else if (trimestresCotises >= 90) {
                return dateNaissanceLocal.plusYears(57);
            } else if (trimestresCotises >= 80) {
                return dateNaissanceLocal.plusYears(58);
            } else if (trimestresCotises >= 70) {
                return dateNaissanceLocal.plusYears(59);
            }
        } else if (anneeNaissance >= 1970 && anneeNaissance <= 1972) {
            if (trimestresCotises >= 111) {
                return dateNaissanceLocal.plusYears(55);
            } else if (trimestresCotises >= 101) {
                return dateNaissanceLocal.plusYears(56);
            } else if (trimestresCotises >= 91) {
                return dateNaissanceLocal.plusYears(57);
            } else if (trimestresCotises >= 81) {
                return dateNaissanceLocal.plusYears(58);
            } else if (trimestresCotises >= 71) {
                return dateNaissanceLocal.plusYears(59);
            }
        } else {
            if (trimestresCotises >= 112) {
                return dateNaissanceLocal.plusYears(55);
            } else if (trimestresCotises >= 102) {
                return dateNaissanceLocal.plusYears(56);
            } else if (trimestresCotises >= 92) {
                return dateNaissanceLocal.plusYears(57);
            } else if (trimestresCotises >= 82) {
                return dateNaissanceLocal.plusYears(58);
            } else if (trimestresCotises >= 72) {
                return dateNaissanceLocal.plusYears(59);
            }
        }

        // Par défaut, aucune condition remplie
        throw new IllegalArgumentException("Les conditions d'âge et de trimestres cotisés ne sont pas remplies.");
    }
}
