package com.cuongd.study.leetcode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Lc0489 {
    private Robot robot;
    private RobotDir dir;
    private Set<List<Integer>> seen;

    public void cleanRoom(Robot robot) {
        this.robot = robot;
        dir = RobotDir.UP;
        seen = new HashSet<>();
        seen.add(List.of(0, 0));
        clean(List.of(0, 0), List.of(0, 0));
    }

    private void clean(List<Integer> pos, List<Integer> prevPos) {
        robot.clean();
        int x = pos.get(0), y = pos.get(1);
        List<Integer> up = List.of(x - 1, y);
        if (!seen.contains(up)) {
            seen.add(up);
            if (moveUp()) clean(up, pos);
        }
        List<Integer> left = List.of(x, y - 1);
        if (!seen.contains(left)) {
            seen.add(left);
            if (moveLeft()) clean(left, pos);
        }
        List<Integer> down = List.of(x + 1, y);
        if (!seen.contains(down)) {
            seen.add(down);
            if (moveDown()) clean(down, pos);
        }
        List<Integer> right = List.of(x, y + 1);
        if (!seen.contains(right)) {
            seen.add(right);
            if (moveRight()) clean(right, pos);
        }
        if (pos.equals(prevPos)) return;
        moveBack(pos, prevPos);
    }

    private boolean moveUp() {
        switch (dir) {
            case LEFT:
                robot.turnRight();
                break;
            case DOWN:
                robot.turnLeft();
                robot.turnLeft();
                break;
            case RIGHT:
                robot.turnLeft();
                break;
        }
        dir = RobotDir.UP;
        return robot.move();
    }

    private boolean moveLeft() {
        switch (dir) {
            case UP:
                robot.turnLeft();
                break;
            case DOWN:
                robot.turnRight();
                break;
            case RIGHT:
                robot.turnLeft();
                robot.turnLeft();
                break;
        }
        dir = RobotDir.LEFT;
        return robot.move();
    }

    private boolean moveDown() {
        switch (dir) {
            case UP:
                robot.turnLeft();
                robot.turnLeft();
                break;
            case LEFT:
                robot.turnLeft();
                break;
            case RIGHT:
                robot.turnRight();
                break;
        }
        dir = RobotDir.DOWN;
        return robot.move();
    }

    private boolean moveRight() {
        switch (dir) {
            case UP:
                robot.turnRight();
                break;
            case LEFT:
                robot.turnLeft();
                robot.turnLeft();
                break;
            case DOWN:
                robot.turnLeft();
                break;
        }
        dir = RobotDir.RIGHT;
        return robot.move();
    }

    private void moveBack(List<Integer> from, List<Integer> to) {
        int fromX = from.get(0), fromY = from.get(1), toX = to.get(0), toY = to.get(1);

        if (toX == fromX - 1 && toY == fromY) moveUp();
        if (toX == fromX && toY == fromY - 1) moveLeft();
        if (toX == fromX + 1 && toY == fromY) moveDown();
        if (toX == fromX && toY == fromY + 1) moveRight();
    }

    private enum RobotDir {
        UP, DOWN, LEFT, RIGHT;
    }

    private interface Robot {
        // Returns true if the cell in front is open and robot moves into the cell.
        // Returns false if the cell in front is blocked and robot stays in the current cell.
        public boolean move();

        // Robot will stay in the same cell after calling turnLeft/turnRight.
        // Each turn will be 90 degrees.
        public void turnLeft();

        public void turnRight();

        // Clean the current cell.
        public void clean();
    }
}
