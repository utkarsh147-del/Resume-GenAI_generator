package com.project.resumeproject.service;



import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorAuthservice {

    private final CodeVerifier verifier;
    private final QrGenerator qrGenerator;

    public TwoFactorAuthservice() {
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        this.verifier = new DefaultCodeVerifier(codeGenerator, new SystemTimeProvider());
        this.qrGenerator = new ZxingPngQrGenerator();
    }

    public String generateSecret() {
        return new DefaultSecretGenerator().generate();
    }

    public byte[] generateQrCode(String secret, String username) {
        QrData qrData = new QrData.Builder()
                .label(username)
                .secret(secret)
                .issuer("ResumeBuilderAPI")
                .build();
        try {
            return qrGenerator.generate(qrData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    public boolean verifyCode(String secret, String code) {
        return verifier.isValidCode(secret, code);
    }
}
