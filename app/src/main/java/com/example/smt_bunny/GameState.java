package com.example.smt_bunny;
// import static com.example.smt_bunny.MainActivity.startTrial;

import static android.os.SystemClock.uptimeMillis;

import android.util.Log;

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
    private static int startingBlockNumber;

    public static String getCurrentPathType(){
        return getPathType(getBlockNumber());
    }
    public static String getCurrentDirection(){
        return getDirection(getBlockNumber());
    }
    public static String getPathType(int bn){
        switch(bn){
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
    public static String getDirection(int bn){
        switch(bn){
            case 1:
            case 3:
                if(getPathType(bn).equals("straight")){
                    return straightStartingDirection;
                } else {
                    return curvedStartingDirection;
                }
            case 2:
            case 4:
                if(getPathType(bn).equals("straight")){
                    return straightSecondDirection;
                } else {
                    return curvedSecondDirection;
                }
            default:
                return "direction_unknown_block";
        }
    }


    public static void initialize(String startingCondition, String straightDir1, String curvedDir1, int startBlock){
        trialNumber = 0;
        blockNumber = startBlock;
        startingBlockNumber = startBlock;
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
        Log.d ("gamestate", "block "+ blockNumber + ", trial "+trialNumber);

        return endOfBlock;
    }

    public static boolean endGame(){
        return blockNumber > MAX_BLOCKS;
    }

    public static int getTrialNumber() {
        return trialNumber;
    }
    public static int getBlockNumber() {
        // allows for blocks > max to continue cycling 1 to 4. Useful for running 'extra' blocks,
        // while endgame continues to be true for any blocks past the endpoint.
        return(blockNumber - startingBlockNumber + 1);
        //return ((blockNumber-1) % MAX_BLOCKS) + 1;
    }
    public static int getActualBlockNumber(){
        return blockNumber;
    }
    public static boolean isLastBlockPlus1() {return getBlockNumber() == MAX_BLOCKS+1;}


    public static void setStartingBlockNumber(int startingBlockNumber) {
        GameState.startingBlockNumber = startingBlockNumber;
    }
}
