package mech.mania.starterpack.strategy;

public class ChooseStrategy {
    public static Strategy chooseStrategy(boolean isZombie) {
        // Modify what is returned here to select the strategy your bot will use
        // NOTE: You can use "isZombie" to use two different strategies for humans and zombies (RECOMMENDED!)
        //
        // For example:
        if (isZombie) {
            return new ZombieStrat();
        } else {
            return new HumanStrat();
        }
    }
}
