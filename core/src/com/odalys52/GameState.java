package com.odalys52;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.Viewport;


public class GameState {
    private int boardSize = 30; //How many squares in the board
    private int yOffset = 400; //How high the board is off the bottom
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Controls controls = new Controls();
    private Queue<Bodypart> mBody = new Queue<Bodypart>();
    private float mTimer = 0;
    private Food mFood = new Food(boardSize);
    private int snakeLength = 3;
    private float colorCounter = 0;

    public GameState() {
        mBody.addLast(new Bodypart(15, 15, boardSize)); //head
        mBody.addLast(new Bodypart(15, 14, boardSize));
        mBody.addLast(new Bodypart(15, 13, boardSize));
    }

    public void update(float delta, Viewport viewport) { //update game logic
        mTimer += delta;
        colorCounter += delta;
        controls.update(viewport);
        if (mTimer > 0.13f){
            mTimer = 0;
            advance();
        }
    }

    private void advance(){
        int headX = mBody.first().getX();
        int headY = mBody.first().getY();

        switch (controls.getDirection()){
            case 0: //up
                mBody.addFirst(new Bodypart(headX,headY+1,boardSize));
                break;
            case 1: //right
                mBody.addFirst(new Bodypart(headX+1,headY,boardSize));
                break;
            case 2: //down
                mBody.addFirst(new Bodypart(headX,headY-1,boardSize));
                break;
            case 3: //left
                mBody.addFirst(new Bodypart(headX-1,headY,boardSize));
                break;
            default://should never happen
            mBody.addFirst(new Bodypart(headX,headY+1,boardSize));
            break;
        }

        if (mBody.first().getX() == mFood.getX() && mBody.first().getY() == mFood.getY()){
            snakeLength++;
            mFood.randomisePos(boardSize); //TODO check it's not in body
        }

        for (int i = 1; i<mBody.size; i++){
            if (mBody.get(i).getX() == mBody.first().getX()
                    && mBody.get(i).getY() == mBody.first().getY()){
                snakeLength = 3;
            }
        }

        while (mBody.size - 1 >= snakeLength){
            mBody.removeLast();
        }
    }

    public void draw(int width, int height, OrthographicCamera camera){ //draw snake and board
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(1, 1, 1, 1); //board outline
        shapeRenderer.rect(0, yOffset, width, width);

        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(0+5, yOffset+5, width-5*2, width-5*2);

        shapeRenderer.setColor(MathUtils.sin(colorCounter), -MathUtils.sin(colorCounter), 1, 1);

        //buttons
        shapeRenderer.rect(235,265,130,135);
        shapeRenderer.rect(235,0,130,135);
        shapeRenderer.rect(105,130,130,130);
        shapeRenderer.rect(365,135,130,130);

        float scaleSnake = width/boardSize; //width of one board square

        //food
        shapeRenderer.rect(mFood.getX() * scaleSnake, mFood.getY()*scaleSnake + yOffset, scaleSnake, scaleSnake);

        for (Bodypart bp : mBody){
            shapeRenderer.rect(bp.getX()*scaleSnake, bp.getY()*scaleSnake + yOffset, scaleSnake, scaleSnake);
        }

        shapeRenderer.end();
    }
}
