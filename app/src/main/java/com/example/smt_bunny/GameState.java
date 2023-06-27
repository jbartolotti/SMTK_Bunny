package com.example.smt_bunny;
// import static com.example.smt_bunny.MainActivity.startTrial;

public class GameState {
    private static int trialNumber;
    private static int blockNumber;
    private static final int MAX_BLOCKS = 4;
    private static final int MAX_TRIALS = 5;
    private static String pathType;
    private static String startingPath;
    private static String secondPath;
    private static String direction;
    private static String straightStartingDirection;
    private static String straightSecondDirection;
    private static String curvedStartingDirection;
    private static String curvedSecondDirection;

    public static String getCurrentPathType(){
        return getPathType(blockNumber);
    }
    public static String getCurrentDirection(){
        return getDirection(blockNumber);
    }
    public static String getPathType(int blocknumber){
        switch(blockNumber){
            case 1:
            case 2:
                return startingPath;
            case 3:
            case 4:
                return secondPath;
            default:
                return "path_unknown_block";
        }
    }
    public static String getDirection(int blocknumber){
        switch(blockNumber){
            case 1:
            case 3:
                if(getPathType(blocknumber).equals("straight")){
                    return straightStartingDirection;
                } else {
                    return curvedStartingDirection;
                }
            case 2:
            case 4:
                if(getPathType(blocknumber).equals("straight")){
                    return straightSecondDirection;
                } else {
                    return curvedSecondDirection;
                }
            default:
                return "direction_unknown_block";
        }
    }


    public static void initialize(String startingCondition, String straightDir1, String curvedDir1){
        trialNumber = 0;
        blockNumber = 1;
        if(startingCondition.equals("straight")){
            startingPath = "straight";
            secondPath = "curved";
        } else {
            startingPath = "curved";
            secondPath = "straight";
        }
        straightStartingDirection = straightDir1;
        curvedStartingDirection = curvedDir1;
        if (straightStartingDirection.equals("leftright")){
            straightSecondDirection = "rightleft";
        } else {
            straightSecondDirection = "leftright";
        }
        if (curvedStartingDirection.equals("leftright")){
            curvedSecondDirection = "rightleft";
        } else {
            curvedSecondDirection = "leftright";
        }

    }

    public static boolean incrementTrial(){
        boolean endOfBlock = false;
        trialNumber++;
        if(trialNumber > MAX_TRIALS){
            trialNumber = 0;
            blockNumber++;
            endOfBlock = true;
        }
        return endOfBlock;
    }

    public static boolean endGame(){
        return blockNumber > MAX_BLOCKS;
    }

    public static int getTrialNumber() {
        return trialNumber;
    }
    public static int getBlockNumber() {
        return blockNumber;
    }


}
