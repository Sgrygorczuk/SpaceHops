package com.packt.spacehops;

class Settings
{
    private boolean[] levelCompletion = new boolean[]{true, true , false, false, false};
    private int[] levelHighScore = new int[]{0,0,0,0,0,0};

    private boolean allLevelsBeatFlag = false;

    boolean[] getLevelCompletion(){return levelCompletion;}

    int[] getLevelHighScore(){return levelHighScore;}

    boolean getAllLevelsBeatFlag(){return  allLevelsBeatFlag;}

    void setLevelCompletion(int levelPosition){
        levelCompletion[levelPosition] = true;
        checkAllLevelsBeat();
    }

    private  void setAllLevelsBeatFlag(){allLevelsBeatFlag = true;}

    private void checkAllLevelsBeat(){
        int counter = 0;
        for (boolean b : levelCompletion) { if (b) { counter++; } }
        if(counter == 5){setAllLevelsBeatFlag();}
    }

    void setHighScore(int levelPosition, int highScore){
        if(checkIfNewHighScore(levelHighScore[levelPosition], highScore)){
            levelHighScore[levelPosition] = highScore;
        }
    }

    private boolean checkIfNewHighScore(int oldHighScore, int newHighScore){
        return oldHighScore < newHighScore;
    }

}
