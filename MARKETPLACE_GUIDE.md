# 🚀 JetBrains Marketplace Yayınlama Rehberi

Bu rehber, Ant Translation Generator plugin'ini JetBrains Marketplace'e yayınlamak için gerekli adımları detaylı bir şekilde açıklar.

## 📋 Yayınlamadan Önce Kontrol Listesi

### ✅ 1. Plugin Kalitesi ve Fonksiyonalite
- [ ] Tüm özellikler test edildi ve çalışıyor
- [ ] Hata durumları uygun şekilde yönetiliyor
- [ ] Kullanıcı arayüzü tutarlı ve kullanıcı dostu
- [ ] Performans sorunları yok
- [ ] Memory leak'ler kontrol edildi

### ✅ 2. Kod Kalitesi
- [ ] Kod temiz ve okunabilir
- [ ] Uygun exception handling mevcut
- [ ] Logging düzgün yapılıyor
- [ ] Dead code temizlendi
- [ ] TODO/FIXME yorumları temizlendi

### ✅ 3. Dokümantasyon
- [ ] README.md detaylı ve güncel
- [ ] Plugin açıklaması net ve anlaşılır
- [ ] Kullanım örnekleri mevcut
- [ ] Troubleshooting bölümü var
- [ ] Changelog hazırlandı

### ✅ 4. Yasal ve Güvenlik
- [ ] Lisans dosyası eklendi (MIT)
- [ ] Üçüncü parti kütüphanelerin lisansları kontrol edildi
- [ ] Güvenlik açıkları tarandı
- [ ] API key'ler güvenli şekilde saklanıyor

### ✅ 5. Plugin Metadata
- [ ] plugin.xml dosyası doğru yapılandırıldı
- [ ] Versiyon numarası belirlendi
- [ ] Desteklenen IDE versiyonları doğru
- [ ] Plugin açıklaması ve özellikleri güncel

## 🔧 Plugin.xml Son Kontrolleri

Plugin'inizi yayınlamadan önce `plugin.xml` dosyasını kontrol edin:

```xml
<idea-plugin>
    <!-- Benzersiz plugin ID -->
    <id>com.ant.translation</id>
    
    <!-- Plugin adı (marketplace'te görünecek) -->
    <name>Ant Translation Generator</name>
    
    <!-- Versiyon numarası -->
    <version>1.0.0</version>
    
    <!-- Vendor bilgileri -->
    <vendor email="info@fatihkacar.com" url="https://fatihkacar.com">ANT</vendor>
    
    <!-- Detaylı açıklama -->
    <description><![CDATA[
        Powerful IntelliJ IDEA plugin for internationalization (i18n).
        Generate translation keys instantly with AI-powered auto-translation using OpenAI GPT-4.
        
        Features:
        • Instant translation key generation
        • AI-powered auto translation with GPT-4
        • Multi-language file management
        • Smart key management
        • 70+ supported languages
    ]]></description>
    
    <!-- Desteklenen IDE versiyonları -->
    <idea-version since-build="231" until-build="241.*"/>
    
    <!-- Bağımlılıklar -->
    <depends>com.intellij.modules.platform</depends>
</idea-plugin>
```

## 📦 Build ve Packaging

### 1. Plugin Build
```bash
# Plugin'i build edin
./gradlew buildPlugin

# Build edilen dosya şurada olacak:
# build/distributions/rider-extension-1.0.0.zip
```

### 2. Plugin Test
```bash
# Plugin'i test IDE'de çalıştırın
./gradlew runIde

# Plugin verification
./gradlew verifyPlugin
```

### 3. Changelog Hazırlama
`CHANGELOG.md` dosyası oluşturun:

```markdown
# Changelog

## [1.0.0] - 2024-01-XX

### Added
- Initial release
- Translation key generation with hierarchical structure
- AI-powered auto translation with OpenAI GPT-4
- Multi-language file management
- Smart key removal functionality
- Support for 70+ languages
- Customizable keyboard shortcuts
- Context menu integration

### Features
- Generate translation keys: Shift + Ctrl + T
- Remove translation keys: Shift + Ctrl + D
- Auto-translate to multiple languages
- JSON file validation and formatting
- Project-relative path configuration
```

## 🌐 JetBrains Marketplace'e Yayınlama

### 1. JetBrains Account Oluşturma
1. [JetBrains Account](https://account.jetbrains.com/) oluşturun
2. [JetBrains Marketplace](https://plugins.jetbrains.com/) hesabınızla giriş yapın

### 2. Plugin Upload
1. [Plugin Upload Sayfası](https://plugins.jetbrains.com/plugin/add)na gidin
2. "Upload plugin" butonuna tıklayın
3. Build edilen `.zip` dosyasını yükleyin
4. Plugin bilgilerini doldurun:

#### Plugin Bilgileri:
- **Name**: Ant Translation Generator
- **Summary**: AI-powered translation key generator for IntelliJ IDEs
- **Description**: README.md'deki açıklamayı kullanın
- **Category**: Code tools
- **Tags**: translation, i18n, internationalization, ai, openai, gpt

#### Pricing:
- **License**: Free
- **Pricing Model**: Free

#### Screenshots:
En az 3-5 screenshot ekleyin:
1. Plugin ayar ekranı
2. Translation key generation örneği
3. Auto translate özelliği
4. Context menu
5. Generated JSON files

### 3. Plugin İnceleme Süreci
- JetBrains team plugin'inizi inceleyecek (1-5 iş günü)
- Sorun varsa email ile bildirilecek
- Onaylandıktan sonra marketplace'te yayınlanacak

## 📈 Yayınlama Sonrası

### 1. Monitoring
- Plugin indirme sayılarını takip edin
- Kullanıcı yorumlarını okuyun
- Bug report'ları kontrol edin

### 2. Güncelleme Süreci
```bash
# Yeni versiyon için:
# 1. plugin.xml'de version'ı güncelleyin
# 2. CHANGELOG.md'yi güncelleyin
# 3. Build edin
./gradlew buildPlugin

# 4. Marketplace'te "Update plugin" ile yeni versiyonu yükleyin
```

### 3. Marketing
- GitHub repository'yi güncel tutun
- Social media'da paylaşın
- Blog yazısı yazın
- Developer community'lerde tanıtın

## 🚨 Yaygın Hatalar ve Çözümleri

### 1. Plugin ID Çakışması
**Hata**: Plugin ID zaten kullanımda
**Çözüm**: Benzersiz bir ID seçin (örn: `com.yourname.pluginname`)

### 2. IDE Uyumluluk Sorunu
**Hata**: Desteklenen IDE versiyonları yanlış
**Çözüm**: `since-build` ve `until-build` değerlerini kontrol edin

### 3. Eksik Bağımlılık
**Hata**: Required dependency eksik
**Çözüm**: `plugin.xml`'de tüm bağımlılıkları tanımlayın

### 4. Büyük Dosya Boyutu
**Hata**: Plugin dosyası çok büyük
**Çözüm**: Gereksiz dosyaları exclude edin, dependencies'i optimize edin

## 📊 Başarı Metrikleri

### İlk Ay Hedefleri:
- [ ] 100+ indirme
- [ ] 4+ yıldız rating
- [ ] 5+ pozitif yorum
- [ ] 0 kritik bug

### 3 Ay Hedefleri:
- [ ] 1000+ indirme
- [ ] 4.5+ yıldız rating
- [ ] 20+ pozitif yorum
- [ ] Community feedback'e göre yeni özellikler

## 🔗 Faydalı Linkler

- [JetBrains Plugin Development](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [Plugin Marketplace Guidelines](https://plugins.jetbrains.com/docs/marketplace/plugin-overview-page.html)
- [Plugin Verification](https://plugins.jetbrains.com/docs/intellij/plugin-verifier.html)
- [Marketing Best Practices](https://plugins.jetbrains.com/docs/marketplace/marketing.html)

## 💡 Pro Tips

1. **Beta Testing**: Plugin'i yayınlamadan önce beta kullanıcılarla test edin
2. **Documentation**: Detaylı dokümantasyon kullanıcı memnuniyetini artırır
3. **Community**: GitHub issues'ı aktif tutun, kullanıcı sorularını hızlı yanıtlayın
4. **Updates**: Düzenli güncellemeler plugin'in canlı olduğunu gösterir
5. **Feedback**: Kullanıcı feedback'ini ciddiye alın ve hızlı implement edin

---

**Başarılar! 🎉**

Plugin'iniz marketplace'te yayınlandıktan sonra community'den gelen feedback'leri değerlendirmeyi ve sürekli geliştirmeyi unutmayın. 