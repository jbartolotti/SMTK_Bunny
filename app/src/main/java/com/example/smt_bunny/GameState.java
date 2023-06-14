package com.example.smt_bunny;
import static com.example.smt_bunny.MainActivity.startTrial;

public class GameState {
    private int trialNumber;
    private int blockNumber;
    public static final int MAX_BLOCKS = 4;
    private static final int MAX_TRIALS = 5;
    private String pathType;


    public String determinePathType(int blocknumber){
        switch(blockNumber){
            case 1:
            case 2:
                return "straight";
            case 3:
            case 4:
                return "curved";
            default:
                return "unknown_path";
        }
    }



    public void incrementTrial(){
        trialNumber++;
        if(trialNumber > MAX_TRIALS){
            trialNumber = 0;
            blockNumber++;
        }
    }
    public int getTrialNumber() {
        return trialNumber;
    }
    public int getBlockNumber() {
        return blockNumber;
    }
    public String getPathType(){
        return pathType;
    }

    public void nextTrial(){
        if (trialNumber == 0){
            //startInterBlock();
            incrementTrial();
        }
        startTrial(trialNumber, pathType);
    }

    public void goToInterBlockView(int blockNumber){

    }
    public void endGame(){

    }

}
