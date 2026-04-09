package za.ac.cput.votingserver.domain;

import java.io.Serializable;

/**
 *
 * @author Hadley Booysen
 */
public class Car implements Serializable {
//car attributes

    private String vehicle_name;
    private String category;
    private int votes;

    public Car() {
    }

    public Car(String vehicle_name, String category) {
        this.vehicle_name = vehicle_name;
        this.category = category;
    }

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Car(String vehicle_name, String category, int votes) {
        this.vehicle_name = vehicle_name;
        this.category = category;
        this.votes = votes;
    }

    public void increaseVotes() {
        this.votes++;
    }

    @Override
    public String toString() {
        return String.format("%-15s\t  %-15s\t  %-5d|\n",
                vehicle_name, category, votes);
    }
}
