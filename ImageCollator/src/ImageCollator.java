import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageCollator extends JFrame {

    private BufferedImage collageImage;
    private JPanel collagePanel;
    private int currentX = 0; // Keep track of the current X position for the next image

    public ImageCollator() {
        setDarkMode();

        collagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(collageImage, 0, 0, this);
            }
        };

        JButton pasteButton = new JButton("Paste Image");
        pasteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteImage();
            }
        });

        JButton clearButton = new JButton("Clear Collage");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearCollage();
            }
        });

        JButton copyButton = new JButton("Copy Collage");
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyCollageToClipboard();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(pasteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(copyButton);

        setLayout(new BorderLayout());
        add(collagePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack(); // Pack the frame to its preferred size
        setLocationRelativeTo(null);
        setTitle("Image Collage App");
        setVisible(true);
    }

    private void pasteImage() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(this);

        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                Image pastedImage = (Image) transferable.getTransferData(DataFlavor.imageFlavor);

                // Initialize collageImage if it's null
                if (collageImage == null) {
                    collageImage = new BufferedImage(pastedImage.getWidth(null) + 10, pastedImage.getHeight(null),
                            BufferedImage.TYPE_INT_ARGB);
                }

                updateCollageSize(pastedImage.getWidth(null));

                Graphics2D g2d = collageImage.createGraphics();
                g2d.drawImage(pastedImage, currentX, 0, null);
                g2d.dispose();

                // Update the currentX position for the next image
                currentX += pastedImage.getWidth(null) + 10; // Add 10 pixels spacing

                collagePanel.repaint();
            } catch (UnsupportedFlavorException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void clearCollage() {
        collageImage = null; // Reset collageImage to null
        currentX = 0; // Reset the currentX position
        collagePanel.repaint();
    }

    private void updateCollageSize(int imageWidth) {
        if (collageImage != null) {
            int newWidth = Math.max(collageImage.getWidth(), currentX + imageWidth + 10);
            int height = collageImage.getHeight();
            BufferedImage newCollageImage = new BufferedImage(newWidth, height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = newCollageImage.createGraphics();
            g2d.drawImage(collageImage, 0, 0, null);
            g2d.dispose();

            collageImage = newCollageImage;
        }
    }

    private void copyCollageToClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        TransferableImage transferableImage = new TransferableImage(collageImage);
        clipboard.setContents(transferableImage, null);
    }

    private void setDarkMode() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

            // Set dark mode colors
            UIManager.put("nimbusBase", new Color(18, 30, 49));
            UIManager.put("nimbusBlueGrey", new Color(100, 140, 200));
            UIManager.put("control", new Color(18, 30, 49));
            UIManager.put("text", new Color(255, 255, 255));
            UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
            UIManager.put("nimbusFocus", new Color(100, 140, 200));

            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ImageCollator();
            }
        });
    }
}

class TransferableImage implements Transferable {
    private Image image;

    public TransferableImage(Image image) {
        this.image = image;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DataFlavor.imageFlavor };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.imageFlavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(DataFlavor.imageFlavor)) {
            return image;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}