import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class EncryptionController {

	private KeyPair keyPair;
	
	private KeyPair prevKeyPair;
	
	@FXML
	private Button open;
	@FXML
	private Button encrypt;
	@FXML
	private Button decrypt;
	@FXML
	private Button keyEncrypt;
	@FXML
	private Button keyDecrypt;
	@FXML
	private ImageView imgBox;
	@FXML
	private ImageView imgBox1;


	private File file;
	public static final String symmetricAlgo = "AES";
	public static final String asymmetricAlgo = "RSA";
	private SecretKey key;
	private BufferedImage originalImage;
	private int imgWidth;
	private int imgHeight;
	private File encrypedFile = new File("C:/Users/Pascal/Pictures/encrypted.jpg");
	
	
	public void init(){
		Security.addProvider(new BouncyCastleProvider());
		// Leerer Schlüssel erzeugen
		SecretKey key = null;
		try {		
			// Initialisierung des Keygenerators aus Performancegründen
			KeyGenerator keyGen = KeyGenerator.getInstance(symmetricAlgo, "BC");
			keyGen.init(256);
			key = keyGen.generateKey();
			// Vergessen des Schlüssels
			key = null;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private SecretKey generateKey() {
		try {
			Security.addProvider(new BouncyCastleProvider());
			// Start der Generierung, AES wird vorgegeben
			KeyGenerator keyGen = KeyGenerator.getInstance(symmetricAlgo, "BC");
			keyGen.init(128);
			// Erzeugung des Schlüssels
			key = keyGen.generateKey();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Rückgabe des Schlüssels
		return key;
	}

	@FXML
	private void openImage() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Bild öffnen");
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"), new FileChooser.ExtensionFilter("GIF", "*.gif"));
		this.file = fc.showOpenDialog(new Stage());
		Image img = new Image(file.toURI().toString());
		imgBox.setImage(img);
		imgWidth = (int) img.getWidth();
		imgHeight = (int) img.getHeight();
		
	}

	private byte[] getImageData(File file) throws IOException{
		BufferedImage image = ImageIO.read(file);
		int w = image.getWidth(), h = image.getHeight();
		int[] argbArray = new int[ w * h ];
		image.getRGB( 0 /* startX */, 0 /* startY  */,
		              w,  h, argbArray,
		              0 /* offset */, w /* scansize */ );
		ByteBuffer byteBuffer = ByteBuffer.allocate(argbArray.length*4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(argbArray);	
		byte[] array = byteBuffer.array();
		return array;
	} 	

	
	private void setEncryptedImage(byte[] encryptedImage){
		IntBuffer intBuf = ByteBuffer.wrap(encryptedImage).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
				 int[] array = new int[intBuf.remaining()];
				 intBuf.get(array);
		BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, imgWidth, imgHeight, array, 0, imgWidth);
		File outputFile = new File("C:/Users/Pascal/Pictures/encrypted.jpg");
		try {
			ImageIO.write(image, "jpg", outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@FXML
	private void encrypt() {
		// Schlüssel erzeugen und speichern
		key = generateKey();
		// saveKeyInFile(key);
		try {
			// Verschlüsselungschipher erzeugen und setzen
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			// Daten verschlüsseln und in ByteArray schreiben
			byte[] encryptedData = cipher.doFinal(getImageData(file));
			setEncryptedImage(encryptedData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void decrypt() {
		
		try {
		// Daten entschlüsseln
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ivParameterSpec = new IvParameterSpec(key.getEncoded());
		cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
		// Daten entschlüsseln imd in Bytearray schreiben
		byte[] decryptedData = cipher.doFinal(getImageData(encrypedFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


//	private void saveKeyInFile(SecretKey key) {
//		try {
//			// Schlüssel in Bytes zerlegen und in ein Byte-Array ablegen
//			byte[] keyByteArray = key.getEncoded();
//			// OutputStream erzeugen und Bytes mitgeben
//			FileChooser fs = new FileChooser();
//			// Set extension
//			fs.setTitle("Key speichern");
//			fs.getExtensionFilters().add(new FileChooser.ExtensionFilter("KEY Dateien", "*.key"));
//			// Anzeigen des Speicherndialog
//			File keyFile = fs.showSaveDialog(new Stage());
//			FileOutputStream fos = new FileOutputStream(keyFile);
//			fos.write(keyByteArray);
//			fos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private SecretKey readKeyFromFile() {
//		FileChooser fc = new FileChooser();
//		fc.setTitle("Key öffnen");
//		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("KEY Dateien", "*.key"));
//		File file = fc.showOpenDialog(new Stage());
//		// in diesem Objekt wird der Schlüssel eingelesen
//		SecretKey readKey = null;
//		// Stream für das Lesen
//		FileInputStream fs = null;
//
//		// Prüfen ob die Datei existiert
//		if (file.exists() && file.canRead()) {
//			try {
//				fs = new FileInputStream(file);
//				// Byte-Array für den Schlüssel vorbereiten
//				byte[] key = new byte[(int) file.length()];
//				// Lesen
//				fs.read(key);
//				// Schlüssel wiederherstellen
//				readKey = new SecretKeySpec(key, symmetricAlgo);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else {
//			Alert alert = new Alert(AlertType.ERROR);
//			alert.setTitle("Error Dialog");
//			alert.setHeaderText("Datei nicht lesbar!");
//			alert.setContentText("Datei existiert nicht oder kann nicht gelesen werden!");
//			alert.showAndWait();
//		}
//		// Rückgabe des Schlüssels
//		return readKey;
//	}
}
