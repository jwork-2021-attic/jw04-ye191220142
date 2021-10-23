/*
 * Copyright (C) 2015 Aeranythe Echosong
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package screen;

import world.*;
import asciiPanel.AsciiPanel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aeranythe Echosong
 */
public class PlayScreen implements Screen {

    private World world;
    private WorldBuilder worldBuilder;
    private int[][] maze; //用来dfs
    private boolean change; //用来判断当前是否由手动切换为dfs，如果是的话把之前dfs的路径进行清空重新计算
    private Creature player;
    private int screenWidth;
    private int screenHeight;
    private List<String> messages;
    private List<String> oldMessages;

    public PlayScreen() {
        this.screenWidth = 50;
        this.screenHeight = 50;
        createWorld();
        this.messages = new ArrayList<String>();
        this.oldMessages = new ArrayList<String>();

        CreatureFactory creatureFactory = new CreatureFactory(this.world);
        createCreatures(creatureFactory);

        change = false;
    }

    private void createCreatures(CreatureFactory creatureFactory) {
        //this.player = creatureFactory.newPlayer(this.messages);
        this.player = creatureFactory.newPlayerAtLocation(messages, 0, 0);

        /*for (int i = 0; i < 8; i++) {
            creatureFactory.newFungus();
        }*/
    }

    private void createWorld() {
        //world = new WorldBuilder(60, 60).makeCaves().build();
        worldBuilder = new WorldBuilder(this.screenWidth, this.screenHeight);
        world = worldBuilder.buildMaze().build();
        maze = worldBuilder.getMaze(); // 0是墙，1未走过，2是当前的路线，3是回头路
    }

    private void clear(){
        for(int[] row : maze)
            for(int x : row)
                if(x > 1)
                    x = 1;
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        // Show terrain
        for (int x = 0; x < screenWidth; x++) {
            for (int y = 0; y < screenHeight; y++) {
                int wx = x + left;
                int wy = y + top;

                if (player.canSee(wx, wy)) {
                    terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
                } else {
                    terminal.write(world.glyph(wx, wy), x, y, Color.DARK_GRAY);
                }
            }
        }
        // Show creatures
        for (Creature creature : world.getCreatures()) {
            if (creature.x() >= left && creature.x() < left + screenWidth && creature.y() >= top
                    && creature.y() < top + screenHeight) {
                if (player.canSee(creature.x(), creature.y())) {
                    terminal.write(creature.glyph(), creature.x() - left, creature.y() - top, creature.color());
                }
            }
        }
        // Creatures can choose their next action now
        world.update();
    }

    private void displayMessages(AsciiPanel terminal, List<String> messages) {
        int top = this.screenHeight - messages.size();
        for (int i = 0; i < messages.size(); i++) {
            terminal.write(messages.get(i), 1, top + i + 1);
        }
        this.oldMessages.addAll(messages);
        messages.clear();
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        // Terrain and creatures
        displayTiles(terminal, getScrollX(), getScrollY());
        // Player
        terminal.write(player.glyph(), player.x() - getScrollX(), player.y() - getScrollY(), player.color());
        // Stats
        //String stats = String.format("%3d/%3d hp", player.hp(), player.maxHP());
        //terminal.write(stats, 1, 23);
        // Messages
        //displayMessages(terminal, this.messages);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        CreatureFactory creatureFactory = new CreatureFactory(world);
        boolean already = false;
        switch (key.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                moveRight();
                break;
            case KeyEvent.VK_UP:
                moveUp();
                break;
            case KeyEvent.VK_DOWN:
                moveDown();
                break;
            case KeyEvent.VK_ENTER:
                if(change == true){
                    clear();
                    change = false;
                }
                dfs();
                break;
        }
        if(player.hp() == 0){
            return new LoseScreen();
        }
        else if (player.x() == world.width() - 1 && player.y() == world.height() - 1){
            return new WinScreen();
        }
        else
            return this;
    }

    private void dfs(){ //一次只走一步，所以不需要递归，回头路可以根据maze数组的值来判断
        if(player.x() < this.screenWidth - 1 && maze[player.x() + 1][player.y()] == 1) {
            maze[player.x()][player.y()] = 2;
            moveRight();
        }
        else if(player.y() < this.screenHeight - 1 && maze[player.x()][player.y() + 1] == 1){
            maze[player.x()][player.y()] = 2;
            moveDown();
        }
        else if(player.x() > 0 && maze[player.x() - 1][player.y()] == 1){
            maze[player.x()][player.y()] = 2;
            moveLeft();
        }
        else if(player.y() > 0 && maze[player.x()][player.y() - 1] == 1){
            maze[player.x()][player.y()] = 2;
            moveUp();
        }
        else if(player.x() < this.screenWidth - 1 && maze[player.x() + 1][player.y()] == 2) {
            maze[player.x()][player.y()] = 3;
            moveRight();
        }
        else if(player.y() < this.screenHeight - 1 && maze[player.x()][player.y() + 1] == 2){
            maze[player.x()][player.y()] = 3;
            moveDown();
        }
        else if(player.x() > 0 && maze[player.x() - 1][player.y()] == 2){
            maze[player.x()][player.y()] = 3;
            moveLeft();
        }
        else if(player.y() > 0 && maze[player.x()][player.y() - 1] == 2){
            maze[player.x()][player.y()] = 3;
            moveUp();
        }
        else{
            return;
        }
    }

    private void moveLeft(){
        boolean already = false;
        change = true;
        if(world.creature(player.x() - 1, player.y()) != null){ //如果走回头路的话
            already = true;
        }
        player.moveBy(-1, 0);
        if(already == true){
            new CreatureFactory(world).newArrow(4, player.x() + 1, player.y());
        }
        else{
            new CreatureFactory(world).newArrow(3, player.x() + 1, player.y());
        }
    }
    private void moveRight(){
        boolean already = false;
        change = true;
        if(world.creature(player.x() + 1, player.y()) != null){
            already = true;
        }
        player.moveBy(1, 0);
        if(already == true){
            new CreatureFactory(world).newArrow(4, player.x() - 1, player.y());
        }
        else{
            new CreatureFactory(world).newArrow(2, player.x() - 1, player.y());
        }
    } 
    private void moveUp(){
        boolean already = false;
        change = true;
        if(world.creature(player.x(), player.y() - 1) != null){
            already = true;
        }
        player.moveBy(0, -1);
        if(already == true){
            new CreatureFactory(world).newArrow(4, player.x(), player.y() + 1);
        }
        else{
            new CreatureFactory(world).newArrow(0, player.x(), player.y() + 1);
        }
    } 
    private void moveDown(){
        boolean already = false;
        change = true;
        if(world.creature(player.x(), player.y() + 1) != null){
            already = true;
        }
        player.moveBy(0, 1);
        if(already == true){
            new CreatureFactory(world).newArrow(4, player.x(), player.y() - 1);
        }
        else{
            new CreatureFactory(world).newArrow(1, player.x(), player.y() - 1);
        }
    } 


    public int getScrollX() {
        return Math.max(0, Math.min(player.x() - screenWidth / 2, world.width() - screenWidth));
    }

    public int getScrollY() {
        return Math.max(0, Math.min(player.y() - screenHeight / 2, world.height() - screenHeight));
    }

}
