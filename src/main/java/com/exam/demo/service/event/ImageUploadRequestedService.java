package com.exam.demo.service.event;

import com.exam.demo.endpoint.event.model.ImageUploadRequested;
import com.exam.demo.file.bucket.BucketComponent;
import com.exam.demo.mail.Email;
import com.exam.demo.mail.Mailer;
import jakarta.mail.internet.InternetAddress;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImageUploadRequestedService implements Consumer<ImageUploadRequested> {
  private final BucketComponent bucketComponent;
  private final Mailer mailer;

  @SneakyThrows
  @Override
  public void accept(ImageUploadRequested requested) {
    File original = bucketComponent.download(requested.getBucketKey());

    File blackAndWhite = convertToBlackAndWhite(original);

    var bwKey = "bw-" + requested.getBucketKey();
    bucketComponent.upload(blackAndWhite, bwKey);
    var uri = bucketComponent.presign(bwKey, Duration.ofDays(1));

    var recipient = new InternetAddress(requested.getEmail());
    var email =
        new Email(
            recipient,
            List.of(),
            List.of(),
            "Votre image en noir et blanc",
            "Voici l'image : " + uri,
            List.of());
    mailer.accept(email);
  }

  private BufferedImage toGray(BufferedImage src) {
    var gray = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    gray.getGraphics().drawImage(src, 0, 0, null);
    return gray;
  }

  @SneakyThrows
  private File convertToBlackAndWhite(File original) {
    BufferedImage image = ImageIO.read(original);
    BufferedImage grayImage = toGray(image);
    File output = File.createTempFile("bw-", ".png");
    ImageIO.write(grayImage, "png", output);
    return output;
  }
}
