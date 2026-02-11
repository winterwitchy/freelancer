import java.util.ArrayList;

public class User {
    public String id;

    public User(String id) {
        this.id = id;
    }

    
    public static Service paint = new Service("paint", 70, 60, 50, 85, 90);
    public static Service web_dev = new Service("web_dev", 95, 75, 85, 80, 90);
    public static Service graphic_design = new Service("graphic_design", 75, 85, 95, 70, 85);
    public static Service data_entry = new Service("data_entry", 50, 50, 30, 95, 95);
    public static Service tutoring = new Service("tutoring", 80, 95, 70, 90, 75);
    public static Service cleaning = new Service("cleaning", 40, 60, 40, 90, 85);
    public static Service writing = new Service("writing", 70, 85, 90, 80, 95);
    public static Service photography = new Service("photography", 85, 80, 90, 75, 90);
    public static Service plumbing = new Service("plumbing", 85, 65, 60, 90, 85);
    public static Service electrical = new Service("electrical", 90, 65, 70, 95, 95);

}
    


class Customer extends User {
    public int cancelled;
    public int totalSpent;
    public int loyaltyPoints;
    public String loyaltyTier;
    public double subsidy;
    public int totalBlacklisted;
    public int totalEmployed;
    public ArrayList<String> blacklistedFreelancers;

    public Customer(String id) {
        super(id);
        this.cancelled = 0;
        this.totalSpent = 0;
        this.loyaltyPoints = 0;
        this.loyaltyTier = "BRONZE";
        this.subsidy = 0;
        this.totalBlacklisted = 0;
        this.totalEmployed = 0;
        this.blacklistedFreelancers = new ArrayList<>();
    }

    public void deductLoyaltyPoints() {
        loyaltyPoints = totalSpent - cancelled*250;
    }

    public void updateSubsidy() {
        switch (loyaltyTier) {
            case "PLATINUM":
                subsidy = 0.15;
                break;
            case "GOLD":
                subsidy = 0.10;
                break;
            case "SILVER":
                subsidy = 0.05;
                break;
            default:
                subsidy = 0;
                break;
        }
    }

    public void updateLoyaltyTier() {
        deductLoyaltyPoints();
        if (loyaltyPoints >= 5000) {
            loyaltyTier = "PLATINUM";
        } else if (loyaltyPoints >= 2000) {
            loyaltyTier = "GOLD";
        } else if (loyaltyPoints >= 500) {
            loyaltyTier = "SILVER";
        } else {
            loyaltyTier = "BRONZE";
        }
        updateSubsidy();
    }

    public Service searchService(String servicename) {
        switch (servicename){
            case "paint":
                return paint;
            case "web_dev":
                return web_dev;
            case "graphic_design":
                return graphic_design;
            case "data_entry":
                return data_entry;
            case "tutoring":
                return tutoring;
            case "cleaning":
                return cleaning;
            case "writing":
                return writing;
            case "photography":
                return photography;
            case "plumbing":
                return plumbing;
            default:
                return electrical;
        }
    }

    public ArrayList<Freelancer> hireBestFreelancer(String string, int k) {
        return searchService(string).heap.getTopK(k, blacklistedFreelancers);
    }
}

class Freelancer extends User {
    public Service service;
    public int[] skills; // T, C, R, E, A
    public int ncompleted; // total completed
    public int ncancelled; // total cancelled
    public int mcompleted; // this month completed
    public int mcancelled; // this month cancelled
    public double averageRating;
    public int compositeScore;
    public double ratingScore;
    public double skillScore;
    public double reliabilityScore;
    public double burnoutPenalty;
    public int price;
    public boolean employed;
    public boolean burnout;
    public Customer cust; // to keep track of which customer employed the freelancer


    
    public Freelancer(String id, String service, int price, int T, int C, int R, int E, int A) {
        super(id);
        this.service = switch (service) {
            case "paint" -> paint;
            case "web_dev" -> web_dev;
            case "graphic_design" -> graphic_design;
            case "data_entry" -> data_entry;
            case "tutoring" -> tutoring;
            case "cleaning" -> cleaning;
            case "writing" -> writing;
            case "photography" -> photography;
            case "plumbing" -> plumbing;
            case "electrical" -> electrical;
            default -> null;
        };
        this.price = price;
        this.skills = new int[]{T, C, R, E, A};
        this.ncompleted = 0;
        this.ncancelled = 0;
        this.mcompleted = 0;
        this.mcancelled = 0;
        this.averageRating = 5.0;
        this.compositeScore = 0;
        this.employed = false;
        this.burnout = false;
        this.burnoutPenalty = 0.0;
        calculateCompositeScore();
    }

    public int compareTo(Freelancer other) {                    // instead of using Comparable, i wrote my own comparator for freelancer
        int res = Integer.compare(this.compositeScore, other.compositeScore);
        if (res == 0) {
            return other.id.compareTo(this.id);
        }
        else {
            return res;
        }
    }

    public void addRating(int rating) {
        ncompleted++;
        mcompleted++;   
        averageRating = (averageRating * (ncompleted + ncancelled) + rating) / (ncompleted + ncancelled + 1);
    }

    public void addCancelPenalty() {
        addRating(0);
        ncompleted--;
        mcompleted--;
        ncancelled++;
        mcancelled++;
        for (int i = 0; i < skills.length; i++) {
            skills[i] = Math.max(0, skills[i] - 3);
        }
    }

    public void applySkillGains() {
        skills[service.importance[0]] = Math.min(100, skills[service.importance[0]] + 2);
        skills[service.importance[1]] = Math.min(100, skills[service.importance[1]] + 1);
        skills[service.importance[2]] = Math.min(100, skills[service.importance[2]] + 1);
    }

    public void updateSkill(int T, int C, int R, int E, int A) {
        skills[0] = T;
        skills[1] = C;
        skills[2] = R;
        skills[3] = E;
        skills[4] = A;
    }

    public Service searchService(String servicename) {
        switch (servicename){
            case "paint":
                return paint;
            case "web_dev":
                return web_dev;
            case "graphic_design":
                return graphic_design;
            case "data_entry":
                return data_entry;
            case "tutoring":
                return tutoring;
            case "cleaning":
                return cleaning;
            case "writing":
                return writing;
            case "photography":
                return photography;
            case "plumbing":
                return plumbing;
            default:
                return electrical;
        }
    }

    
    public void calculateCompositeScore() {
        skillScore = (double) ((double) (skills[0]*service.T + skills[1]*service.C + skills[2]*service.R + skills[3]*service.E + skills[4]*service.A) / (double) (100*(service.T + service.C + service.R + service.E + service.A)));
        ratingScore = averageRating / 5.0;
        reliabilityScore = (ncancelled + ncompleted) == 0 ? 1.0 : 1 - (double) ncancelled / (ncancelled + ncompleted);
        burnoutPenalty = burnout ? 0.45 : 0.0;
        compositeScore = (int) ( 10000 * (0.55 * skillScore + 0.25 * ratingScore + 0.2 * reliabilityScore - burnoutPenalty));
    }
}