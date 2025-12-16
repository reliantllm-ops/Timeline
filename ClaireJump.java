import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;

public class ClaireJump extends JFrame {

    public ClaireJump() {
        setTitle("Claire's Castle Adventure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClaireJump());
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {

    // Game dimensions
    private static final int WIDTH = 600;
    private static final int HEIGHT = 700;

    // Claire properties
    private double claireX = 280;
    private double claireY = 550;
    private double velocityX = 0;
    private double velocityY = 0;
    private boolean isOnGround = true;
    private int facingDirection = 1; // 1 = right, -1 = left
    private static final double GRAVITY = 0.6;
    private static final double JUMP_STRENGTH = -14;
    private static final double MOVE_SPEED = 5;
    private static final int CLAIRE_WIDTH = 35;
    private static final int CLAIRE_HEIGHT = 55;

    // Camera
    private double cameraY = 0;
    private double targetCameraY = 0;

    // Game state
    private boolean gameRunning = true;
    private boolean gameStarted = false;
    private boolean gameWon = false;
    private int currentLevel = 1;
    private int maxLevel = 10;

    // Platforms
    private ArrayList<Platform> platforms = new ArrayList<>();

    // Animation
    private int runFrame = 0;
    private int runAnimTimer = 0;
    private int frameCount = 0;

    // Movement keys
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // Colors
    private Color claireShirt = new Color(255, 105, 180);
    private Color claireHair = new Color(139, 69, 19);
    private Color claireSkin = new Color(255, 218, 185);
    private Color clairePants = new Color(70, 130, 180);

    // Timer
    private Timer gameTimer;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(40, 40, 60));
        setFocusable(true);
        addKeyListener(this);

        initializePlatforms();

        gameTimer = new Timer(16, this);
        gameTimer.start();
    }

    private void initializePlatforms() {
        platforms.clear();

        // Ground floor
        platforms.add(new Platform(0, 620, WIDTH, 80, "ground"));

        // Generate castle platforms going upward
        Random rand = new Random(42); // Fixed seed for consistent level
        int y = 520;
        int levelNum = 1;

        while (y > -maxLevel * 120) {
            int platformWidth = 100 + rand.nextInt(80);
            int x;

            // Alternate sides with some randomness
            if (levelNum % 2 == 0) {
                x = 50 + rand.nextInt(150);
            } else {
                x = WIDTH - 50 - platformWidth - rand.nextInt(150);
            }

            // Keep platforms within bounds
            x = Math.max(20, Math.min(x, WIDTH - platformWidth - 20));

            String type = "stone";
            if (levelNum == maxLevel) {
                type = "goal";
                platformWidth = 150;
                x = WIDTH / 2 - 75;
            } else if (levelNum % 3 == 0) {
                type = "brick";
            }

            platforms.add(new Platform(x, y, platformWidth, 25, type, levelNum));

            y -= 90 + rand.nextInt(30);
            levelNum++;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning && gameStarted && !gameWon) {
            update();
        }
        repaint();
        frameCount++;
    }

    private void update() {
        // Horizontal movement
        velocityX = 0;
        if (leftPressed) {
            velocityX = -MOVE_SPEED;
            facingDirection = -1;
        }
        if (rightPressed) {
            velocityX = MOVE_SPEED;
            facingDirection = 1;
        }

        // Apply gravity
        velocityY += GRAVITY;

        // Update position
        claireX += velocityX;
        claireY += velocityY;

        // Keep Claire within horizontal bounds
        if (claireX < 10) claireX = 10;
        if (claireX > WIDTH - CLAIRE_WIDTH - 10) claireX = WIDTH - CLAIRE_WIDTH - 10;

        // Platform collision
        isOnGround = false;
        for (Platform p : platforms) {
            if (checkPlatformCollision(p)) {
                if (velocityY > 0) {
                    claireY = p.y - CLAIRE_HEIGHT;
                    velocityY = 0;
                    isOnGround = true;

                    // Check for goal
                    if (p.type.equals("goal")) {
                        gameWon = true;
                    }

                    // Update current level
                    if (p.level > currentLevel) {
                        currentLevel = p.level;
                    }
                }
            }
        }

        // Check if fell off bottom
        if (claireY > cameraY + HEIGHT + 100) {
            gameOver();
        }

        // Update camera to follow Claire
        targetCameraY = claireY - HEIGHT / 2;
        if (targetCameraY > 0) targetCameraY = 0;
        cameraY += (targetCameraY - cameraY) * 0.08;

        // Run animation
        if (Math.abs(velocityX) > 0 && isOnGround) {
            runAnimTimer++;
            if (runAnimTimer > 5) {
                runFrame = (runFrame + 1) % 4;
                runAnimTimer = 0;
            }
        } else {
            runFrame = 0;
        }
    }

    private boolean checkPlatformCollision(Platform p) {
        double claireBottom = claireY + CLAIRE_HEIGHT;
        double claireCenterX = claireX + CLAIRE_WIDTH / 2;

        // Check if Claire is above the platform and falling
        if (velocityY >= 0 &&
            claireBottom >= p.y && claireBottom <= p.y + 20 &&
            claireCenterX >= p.x && claireCenterX <= p.x + p.width) {
            return true;
        }
        return false;
    }

    private void gameOver() {
        gameRunning = false;
    }

    private void restartGame() {
        claireX = 280;
        claireY = 550;
        velocityX = 0;
        velocityY = 0;
        isOnGround = true;
        cameraY = 0;
        targetCameraY = 0;
        currentLevel = 1;
        gameRunning = true;
        gameStarted = true;
        gameWon = false;
        initializePlatforms();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Apply camera transform
        g2d.translate(0, -cameraY);

        // Draw background
        drawBackground(g2d);

        // Draw castle walls
        drawCastleWalls(g2d);

        // Draw platforms
        for (Platform p : platforms) {
            drawPlatform(g2d, p);
        }

        // Draw Claire
        drawClaire(g2d);

        // Reset transform for UI
        g2d.translate(0, cameraY);

        // Draw UI
        drawUI(g2d);

        // Draw screens
        if (!gameStarted) {
            drawStartScreen(g2d);
        } else if (gameWon) {
            drawWinScreen(g2d);
        } else if (!gameRunning) {
            drawGameOverScreen(g2d);
        }
    }

    private void drawBackground(Graphics2D g2d) {
        // Castle interior gradient
        int startY = (int) cameraY - 200;
        int endY = (int) cameraY + HEIGHT + 200;

        for (int y = startY; y < endY; y += 50) {
            float ratio = Math.max(0, Math.min(1, (y + 1000) / 2000f));
            Color c = new Color(
                (int)(30 + ratio * 30),
                (int)(30 + ratio * 20),
                (int)(50 + ratio * 30)
            );
            g2d.setColor(c);
            g2d.fillRect(0, y, WIDTH, 50);
        }

        // Torches on walls
        for (int y = 500; y > -maxLevel * 120; y -= 200) {
            drawTorch(g2d, 30, y);
            drawTorch(g2d, WIDTH - 50, y);
        }
    }

    private void drawTorch(Graphics2D g2d, int x, int y) {
        // Torch holder
        g2d.setColor(new Color(80, 50, 30));
        g2d.fillRect(x, y, 20, 8);
        g2d.fillRect(x + 7, y, 6, 25);

        // Flame
        int flicker = (int)(Math.sin(frameCount * 0.3 + x) * 3);
        g2d.setColor(new Color(255, 200, 50, 200));
        g2d.fillOval(x + 3 + flicker, y - 20, 14, 20);
        g2d.setColor(new Color(255, 100, 30, 180));
        g2d.fillOval(x + 5 + flicker, y - 15, 10, 15);
        g2d.setColor(new Color(255, 255, 100, 150));
        g2d.fillOval(x + 7 + flicker, y - 10, 6, 10);

        // Glow
        g2d.setColor(new Color(255, 150, 50, 30));
        g2d.fillOval(x - 30, y - 50, 80, 80);
    }

    private void drawCastleWalls(Graphics2D g2d) {
        // Stone wall texture on sides
        g2d.setColor(new Color(70, 70, 80));

        int startY = (int) cameraY - 200;
        int endY = (int) cameraY + HEIGHT + 200;

        // Left wall
        g2d.fillRect(0, startY, 15, endY - startY);
        // Right wall
        g2d.fillRect(WIDTH - 15, startY, 15, endY - startY);

        // Wall details
        g2d.setColor(new Color(50, 50, 60));
        for (int y = startY; y < endY; y += 30) {
            int offset = (y / 30 % 2) * 15;
            for (int x = 0; x < 15; x += 15) {
                g2d.drawRect(x, y + offset, 14, 29);
            }
            for (int x = WIDTH - 15; x < WIDTH; x += 15) {
                g2d.drawRect(x, y + offset, 14, 29);
            }
        }
    }

    private void drawPlatform(Graphics2D g2d, Platform p) {
        switch (p.type) {
            case "ground":
                // Ground floor
                g2d.setColor(new Color(60, 60, 70));
                g2d.fillRect(p.x, p.y, p.width, p.height);
                g2d.setColor(new Color(80, 80, 90));
                for (int x = p.x; x < p.x + p.width; x += 40) {
                    g2d.drawRect(x, p.y, 39, 25);
                }
                // Carpet
                g2d.setColor(new Color(139, 0, 0));
                g2d.fillRect(WIDTH/2 - 60, p.y, 120, 10);
                g2d.setColor(new Color(218, 165, 32));
                g2d.fillRect(WIDTH/2 - 60, p.y, 120, 3);
                break;

            case "stone":
                // Stone platform
                g2d.setColor(new Color(100, 100, 110));
                g2d.fillRoundRect(p.x, p.y, p.width, p.height, 5, 5);
                g2d.setColor(new Color(80, 80, 90));
                g2d.drawRoundRect(p.x, p.y, p.width, p.height, 5, 5);
                // Stone texture
                g2d.setColor(new Color(70, 70, 80));
                for (int x = p.x + 5; x < p.x + p.width - 10; x += 25) {
                    g2d.drawLine(x, p.y + 5, x, p.y + p.height - 5);
                }
                break;

            case "brick":
                // Brick platform
                g2d.setColor(new Color(139, 69, 50));
                g2d.fillRoundRect(p.x, p.y, p.width, p.height, 5, 5);
                g2d.setColor(new Color(100, 50, 40));
                for (int x = p.x; x < p.x + p.width; x += 20) {
                    int offset = ((x - p.x) / 20 % 2) * 10;
                    g2d.drawRect(x, p.y, 19, p.height - 1);
                }
                break;

            case "goal":
                // Goal platform (throne room)
                g2d.setColor(new Color(218, 165, 32));
                g2d.fillRoundRect(p.x, p.y, p.width, p.height, 8, 8);
                g2d.setColor(new Color(255, 215, 0));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(p.x, p.y, p.width, p.height, 8, 8);

                // Crown/trophy on platform
                int crownX = p.x + p.width/2;
                int crownY = p.y - 40;
                drawCrown(g2d, crownX, crownY);

                // "GOAL" text
                g2d.setColor(new Color(255, 215, 0));
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("THRONE ROOM", p.x + 25, p.y - 50);
                break;
        }

        // Level number
        if (p.level > 0 && !p.type.equals("goal")) {
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("" + p.level, p.x + p.width/2 - 3, p.y + 17);
        }
    }

    private void drawCrown(Graphics2D g2d, int x, int y) {
        // Crown base
        g2d.setColor(new Color(255, 215, 0));
        int[] crownX = {x - 20, x - 15, x - 8, x, x + 8, x + 15, x + 20, x + 15, x - 15};
        int[] crownY = {y + 25, y, y + 15, y - 5, y + 15, y, y + 25, y + 25, y + 25};
        g2d.fillPolygon(crownX, crownY, 9);

        // Jewels
        g2d.setColor(Color.RED);
        g2d.fillOval(x - 5, y + 10, 10, 10);
        g2d.setColor(Color.BLUE);
        g2d.fillOval(x - 17, y + 5, 8, 8);
        g2d.fillOval(x + 9, y + 5, 8, 8);

        // Sparkle
        g2d.setColor(Color.WHITE);
        int sparkle = frameCount % 30 < 15 ? 1 : 0;
        g2d.fillOval(x + 5 + sparkle, y - 2, 4, 4);
    }

    private void drawClaire(Graphics2D g2d) {
        int x = (int) claireX;
        int y = (int) claireY;
        int legOffset = 0;

        // Running animation
        if (Math.abs(velocityX) > 0 && isOnGround) {
            switch (runFrame) {
                case 0: legOffset = -4; break;
                case 1: legOffset = 0; break;
                case 2: legOffset = 4; break;
                case 3: legOffset = 0; break;
            }
        }

        // Flip for direction
        Graphics2D g2 = (Graphics2D) g2d.create();
        if (facingDirection == -1) {
            g2.translate(x + CLAIRE_WIDTH, 0);
            g2.scale(-1, 1);
            x = 0;
        }

        // Shadow
        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillOval((int)claireX + 5, y + CLAIRE_HEIGHT - 3, 25, 8);

        // Legs
        g2.setColor(clairePants);
        g2.fillRoundRect(x + 8 + legOffset, y + 35, 8, 20, 4, 4);
        g2.fillRoundRect(x + 19 - legOffset, y + 35, 8, 20, 4, 4);

        // Shoes
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x + 6 + legOffset, y + 50, 11, 6, 3, 3);
        g2.fillRoundRect(x + 18 - legOffset, y + 50, 11, 6, 3, 3);
        g2.setColor(claireShirt);
        g2.fillRect(x + 7 + legOffset, y + 52, 9, 2);
        g2.fillRect(x + 19 - legOffset, y + 52, 9, 2);

        // Body
        g2.setColor(claireShirt);
        g2.fillRoundRect(x + 5, y + 18, 25, 22, 6, 6);

        // Arms
        g2.setColor(claireSkin);
        int armSwing = !isOnGround ? -8 : legOffset;
        g2.fillRoundRect(x, y + 19 - armSwing/2, 6, 16, 3, 3);
        g2.fillRoundRect(x + 29, y + 19 + armSwing/2, 6, 16, 3, 3);

        // "CLAIRE" on shirt
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 6));
        g2.drawString("CLAIRE", x + 6, y + 32);

        // Neck
        g2.setColor(claireSkin);
        g2.fillRect(x + 13, y + 12, 9, 8);

        // Head
        g2.setColor(claireSkin);
        g2.fillOval(x + 7, y - 3, 21, 18);

        // Hair
        g2.setColor(claireHair);
        g2.fillOval(x + 5, y - 6, 25, 16);
        g2.fillArc(x + 7, y - 4, 21, 14, 0, 180);
        g2.fillOval(x + 24, y - 1, 10, 14);
        g2.fillOval(x + 27, y + 8, 7, 10);

        // Face
        g2.setColor(claireSkin);
        g2.fillOval(x + 9, y, 17, 14);

        // Eyes
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 11, y + 3, 6, 6);
        g2.fillOval(x + 19, y + 3, 6, 6);
        g2.setColor(new Color(70, 130, 180));
        g2.fillOval(x + 13, y + 4, 3, 4);
        g2.fillOval(x + 21, y + 4, 3, 4);
        g2.setColor(Color.BLACK);
        g2.fillOval(x + 13, y + 5, 2, 2);
        g2.fillOval(x + 21, y + 5, 2, 2);

        // Blush
        g2.setColor(new Color(255, 182, 193, 150));
        g2.fillOval(x + 9, y + 8, 4, 2);
        g2.fillOval(x + 22, y + 8, 4, 2);

        // Mouth
        g2.setColor(new Color(200, 100, 100));
        if (!isOnGround) {
            g2.fillOval(x + 15, y + 10, 5, 4);
        } else {
            g2.setStroke(new BasicStroke(1));
            g2.drawArc(x + 14, y + 9, 6, 4, 200, 140);
        }

        g2.dispose();

        // Name tag above Claire
        drawNameTag(g2d, (int)claireX, (int)claireY);
    }

    private void drawNameTag(Graphics2D g2d, int x, int y) {
        int tagY = y - 25 + (int)(Math.sin(frameCount * 0.1) * 2);

        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRoundRect(x - 2, tagY - 3, 45, 18, 8, 8);
        g2d.setColor(claireShirt);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x - 2, tagY - 3, 45, 18, 8, 8);

        // Pointer
        int[] triX = {x + 12, x + 22, x + 17};
        int[] triY = {tagY + 15, tagY + 15, tagY + 21};
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillPolygon(triX, triY, 3);

        g2d.setColor(claireShirt);
        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.drawString("Claire", x + 3, tagY + 10);

        // Heart
        drawHeart(g2d, x + 38, tagY, 7);
    }

    private void drawHeart(Graphics2D g2d, int x, int y, int size) {
        g2d.setColor(claireShirt);
        Path2D heart = new Path2D.Double();
        heart.moveTo(x, y + size/4);
        heart.curveTo(x, y, x - size/2, y, x - size/2, y + size/4);
        heart.curveTo(x - size/2, y + size/2, x, y + size*3/4, x, y + size);
        heart.curveTo(x, y + size*3/4, x + size/2, y + size/2, x + size/2, y + size/4);
        heart.curveTo(x + size/2, y, x, y, x, y + size/4);
        g2d.fill(heart);
    }

    private void drawUI(Graphics2D g2d) {
        // Level indicator
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(WIDTH/2 - 60, 10, 120, 35, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Level: " + currentLevel + " / " + maxLevel, WIDTH/2 - 45, 33);

        // Controls
        if (gameStarted && gameRunning && !gameWon) {
            g2d.setColor(new Color(0, 0, 0, 120));
            g2d.fillRoundRect(10, HEIGHT - 70, 180, 60, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            g2d.drawString("LEFT / RIGHT = Move", 20, HEIGHT - 50);
            g2d.drawString("SPACE / UP = Jump", 20, HEIGHT - 32);
        }
    }

    private void drawStartScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Title box
        g2d.setColor(new Color(60, 50, 70, 240));
        g2d.fillRoundRect(WIDTH/2 - 180, 100, 360, 320, 20, 20);
        g2d.setColor(new Color(218, 165, 32));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(WIDTH/2 - 180, 100, 360, 320, 20, 20);

        // Title
        g2d.setColor(new Color(255, 215, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        String title = "Claire's Castle";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, WIDTH/2 - fm.stringWidth(title)/2, 155);

        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String subtitle = "Adventure";
        fm = g2d.getFontMetrics();
        g2d.drawString(subtitle, WIDTH/2 - fm.stringWidth(subtitle)/2, 185);

        // Crown decoration
        drawCrown(g2d, WIDTH/2, 210);

        // Instructions
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.setColor(Color.WHITE);
        String[] lines = {
            "Help Claire climb the castle!",
            "",
            "LEFT / RIGHT - Move",
            "SPACE or UP - Jump",
            "",
            "Reach the Throne Room to win!"
        };

        int startY = 270;
        for (String line : lines) {
            fm = g2d.getFontMetrics();
            g2d.drawString(line, WIDTH/2 - fm.stringWidth(line)/2, startY);
            startY += 24;
        }

        g2d.setColor(new Color(255, 215, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        String start = "Press SPACE to Start!";
        fm = g2d.getFontMetrics();
        g2d.drawString(start, WIDTH/2 - fm.stringWidth(start)/2, 400);
    }

    private void drawWinScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        g2d.setColor(new Color(60, 50, 70, 240));
        g2d.fillRoundRect(WIDTH/2 - 160, 150, 320, 200, 20, 20);
        g2d.setColor(new Color(255, 215, 0));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(WIDTH/2 - 160, 150, 320, 200, 20, 20);

        drawCrown(g2d, WIDTH/2, 190);

        g2d.setColor(new Color(255, 215, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        String win = "You Win!";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(win, WIDTH/2 - fm.stringWidth(win)/2, 260);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        String msg = "Claire reached the Throne Room!";
        fm = g2d.getFontMetrics();
        g2d.drawString(msg, WIDTH/2 - fm.stringWidth(msg)/2, 295);

        g2d.setColor(new Color(255, 215, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        String restart = "Press SPACE to Play Again!";
        fm = g2d.getFontMetrics();
        g2d.drawString(restart, WIDTH/2 - fm.stringWidth(restart)/2, 335);
    }

    private void drawGameOverScreen(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        g2d.setColor(new Color(60, 50, 70, 240));
        g2d.fillRoundRect(WIDTH/2 - 140, 180, 280, 160, 20, 20);
        g2d.setColor(new Color(200, 50, 50));
        g2d.setStroke(new BasicStroke(4));
        g2d.drawRoundRect(WIDTH/2 - 140, 180, 280, 160, 20, 20);

        g2d.setColor(new Color(200, 50, 50));
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        String over = "Game Over!";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(over, WIDTH/2 - fm.stringWidth(over)/2, 230);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        String level = "Reached Level: " + currentLevel;
        fm = g2d.getFontMetrics();
        g2d.drawString(level, WIDTH/2 - fm.stringWidth(level)/2, 270);

        g2d.setColor(claireShirt);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        String restart = "Press SPACE to Try Again!";
        fm = g2d.getFontMetrics();
        g2d.drawString(restart, WIDTH/2 - fm.stringWidth(restart)/2, 315);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (!gameStarted || gameWon || !gameRunning) {
            if (key == KeyEvent.VK_SPACE) {
                restartGame();
            }
            return;
        }

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if ((key == KeyEvent.VK_SPACE || key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && isOnGround) {
            velocityY = JUMP_STRENGTH;
            isOnGround = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

class Platform {
    int x, y, width, height;
    String type;
    int level;

    public Platform(int x, int y, int width, int height, String type) {
        this(x, y, width, height, type, 0);
    }

    public Platform(int x, int y, int width, int height, String type, int level) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.level = level;
    }
}
