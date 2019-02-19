package video;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.PictureAnalyser;
import controller.PictureEditor;

/**
 * Class shows graphic file content.
 * Offers menubar allowing user to load and save files.
 * @author Adam 'Prorok' Nowik
 */
public class ImageFrame extends JFrame{
	
	private static final long serialVersionUID = 1L;
	public ImageFrame() {
		setTitle("Alfa Romeo Logo Recognition System");
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGH);
		
		JMenu fileMenu = new JMenu("File");
		// adding openItem button
		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener() { // creating actionListener for open menu button
			@Override
			public void actionPerformed(ActionEvent event) {
				openFile();
			}
		});
		fileMenu.add(openItem);
		JMenu saveMenu = new JMenu("Save");
		fileMenu.add(saveMenu);
		Iterator<String> iter = writerFormats.iterator();
		while (iter.hasNext())
		{
			final String formatName = iter.next();
			JMenuItem formatItem = new JMenuItem(formatName);
			saveMenu.add(formatItem);
			formatItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent event) {
					saveFile(formatName);
				}
			});
		}
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);
		
		JMenu pictureMenu = new JMenu("Picture");
		
		
		JMenuItem blurPicture = new JMenuItem("Blur");
		blurPicture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				blurPicture();
				
			}
		});
		pictureMenu.add(blurPicture); 
		
		JMenu borderMenu = new JMenu("Border Extraction");
		pictureMenu.add(borderMenu);
		
		JMenuItem prewittPicture = new JMenuItem("Prewitt");
		prewittPicture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				prewittPicture();
				
			}
		});
		borderMenu.add(prewittPicture); 
		
		JMenuItem sharpenPicture = new JMenuItem("Borders Sharpen");
		sharpenPicture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sharpenPicture();
				
			}
		});
		borderMenu.add(sharpenPicture);
		
		JMenuItem sobellPicture = new JMenuItem("Sobell");
		sobellPicture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sobellPicture();
				
			}
		});
		borderMenu.add(sobellPicture);
		
		JMenu rankingMenu = new JMenu("Ranking filters");
		pictureMenu.add(rankingMenu);
		
		JMenuItem minimumRanking = new JMenuItem("Minimum");
		minimumRanking.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				minimum();
			}
		});
		rankingMenu.add(minimumRanking); 
		
		JMenuItem medianRanking = new JMenuItem("Median");
		medianRanking.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				median();
			}
		});
		rankingMenu.add(medianRanking);
		
		JMenuItem maximumRanking = new JMenuItem("Maximum");
		maximumRanking.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				maximum();
			}
		});
		rankingMenu.add(maximumRanking);
		
		JMenuItem openRanking = new JMenuItem("Open");
		openRanking.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				minimum();
				maximum();
			}
		});
		rankingMenu.add(openRanking);
		
		JMenuItem closeRanking = new JMenuItem("Close");
		closeRanking.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				maximum();
				minimum();
			}
		});
		rankingMenu.add(closeRanking);
		
		JMenuItem blackWhitePicture = new JMenuItem("Black & white");
		blackWhitePicture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				blackWhitePicture();
				
			}
		});
		pictureMenu.add(blackWhitePicture); 
		 
		
		JMenu analyseMenu = new JMenu("Analyse Picture");
		
		JMenuItem analysePicture = new JMenuItem("Analyse Picture");
		analysePicture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				analysePicture();
			}
		});
		
		analyseMenu.add(analysePicture);
		
		JMenuItem filterPicture = new JMenuItem("Filter!");
		filterPicture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				blurPicture();
				sharpenPicture();
				filterPicture();
			}
		});
		analyseMenu.add(filterPicture);

		JMenuItem computeFactors = new JMenuItem("Compute Factors");
		computeFactors.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				computeFactors();
			}
		});
		analyseMenu.add(computeFactors);

		JMenuItem computePattern = new JMenuItem("Search");
		computePattern.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				computePattern();
			}
		});
		analyseMenu.add(computePattern);
		
		analyseMenu.setEnabled(false);
		pictureMenu.setEnabled(false);
		_pictureMenu = pictureMenu;
		_analyseMenu = analyseMenu;
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(pictureMenu);
		menuBar.add(analyseMenu); 

		
		setJMenuBar(menuBar);
	}
	
	/**
	 * Opens the file and loads picture
	 */
	public void openFile()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(".")); // filepath in current folder
		String[] extensions = ImageIO.getReaderFileSuffixes();
		chooser.setFileFilter(new FileNameExtensionFilter("Image files", extensions));
		int r = chooser.showOpenDialog(this);
		if (r != JFileChooser.APPROVE_OPTION) return;
		File f = chooser.getSelectedFile();
		Box box = Box.createVerticalBox();
		try
		{
			String name = f.getName();
			String suffix = name.substring(name.lastIndexOf('.') + 1);
			Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
			ImageReader reader = iter.next();
			ImageInputStream imageIn = ImageIO.createImageInputStream(f);
			reader.setInput(imageIn);
			int count = reader.getNumImages(true);
			images = new BufferedImage[count];
			for (int i = 0; i < count; i ++)
			{
				images[i] = reader.read(i);
				box.add(new JLabel(new ImageIcon(images[i])));
			}
			
			_pictureMenu.setEnabled(true);
			_analyseMenu.setEnabled(true);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(this, e);
		}
		setContentPane(new JScrollPane(box));
		validate();
	}
	
	public void saveFile(final String formatName)
	{
		if (images == null) return;
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(formatName);
		ImageWriter writer = iter.next();
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		String[] extensions = writer.getOriginatingProvider().getFileSuffixes();
		chooser.setFileFilter(new FileNameExtensionFilter("Image files", extensions));
		
		int r = chooser.showSaveDialog(this);
		if (r != JFileChooser.APPROVE_OPTION) return;
		File f = chooser.getSelectedFile();
		try
		{
			ImageOutputStream imageOut = ImageIO.createImageOutputStream(f);
			writer.setOutput(imageOut);
			
			writer.write(new IIOImage(images[0], null, null));
			for (int i = 0; i < images.length; i++)
			{
				IIOImage iioImage = new IIOImage(images[i], null, null);
				if (writer.canInsertImage(i)) writer.writeInsert(i, iioImage, null);
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(this, e);
		}
	}
	
	public void analysePicture()
	{
		PictureAnalyser analyser = new PictureAnalyser(images[0]);
		analyser.analyse();
		repaint();
	}
	
	public void blurPicture()
	{
		PictureEditor editor = new PictureEditor(images[0]);
		editor.blur();
		repaint();
	}
	
	public void prewittPicture()
	{
		PictureEditor editor = new PictureEditor(images[0]);
		editor.prewitt();
		repaint();
	}
	
	public void blackWhitePicture()
	{
		PictureEditor editor = new PictureEditor(images[0]);
		editor.blackWhite();
		repaint();
	}
	
	public void sharpenPicture()
	{
		PictureEditor editor = new PictureEditor(images[0]);
		editor.sharpen();
		repaint();
	}
	
	public void sobellPicture()
	{
		PictureEditor editor = new PictureEditor(images[0]);
		editor.sobell();
		repaint();
	}
	
	public void minimum()
	{
		PictureEditor editor = new PictureEditor(images[0]);
		editor.rankingFilter(-1);
		repaint();
	}
	
	public void median()
	{
		PictureEditor editor = new PictureEditor(images[0]);
		editor.rankingFilter(0);
		repaint();
	}
	
	public void maximum()
	{
		PictureEditor editor = new PictureEditor(images[0]);
		editor.rankingFilter(1);
		repaint();
	}
	
	public void filterPicture()
	{
		PictureEditor editor = new PictureEditor(images[0]);
		editor.blur();
		editor.blur();
		editor.onlyBlue();
		editor.rankingFilter(1);
		editor.rankingFilter(-1);
		repaint();
	}
	
	public void computeFactors()
	{
		PictureAnalyser analyser = new PictureAnalyser(images[0]);
		analyser.computeFactors();
		repaint();
	}
	
	public void computePattern()
	{
		PictureAnalyser analyser = new PictureAnalyser(images[0]);
		PictureEditor editor = new PictureEditor(images[0]);
		editor.blur();
		editor.blur();
		editor.onlyBlue();
		editor.rankingFilter(1);
		editor.rankingFilter(-1);
		analyser.computeFactors();
		repaint();
	}
	
	public static Set<String> getWriterFormats()
	{
		TreeSet<String> writerFormats = new TreeSet<String>();
		TreeSet<String> formatNames = new TreeSet<String>(Arrays.asList(ImageIO.getWriterFormatNames()));
		
		while (formatNames.size() > 0)
		{
			String name = formatNames.iterator().next();
			Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(name);
			ImageWriter writer = iter.next();
			String[] names = writer.getOriginatingProvider().getFormatNames();
			String format = names[0];
			if (format.equals(format.toLowerCase())) format = format.toUpperCase();
			writerFormats.add(format);
			formatNames.removeAll(Arrays.asList(names));
		}
		
		return writerFormats;
	}
	
	private JMenu _pictureMenu;
	private JMenuItem _analyseMenu;

	private BufferedImage[] images;
	private static Set<String> writerFormats = getWriterFormats();
	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGH = 600;
}
