import RekognitionTextApi as re
import ComprehendApi as co
import TranslateApi as tr
import PollyApi as po

rekognition = re.connect()
comprehend = co.connect()
translate = tr.connect()
polly = po.connect()

text = re.detectText(rekognition, 'jsimon-public-us', 'billboard.jpg')
print(text)

language = co.detectLanguage(comprehend, text)
print(language)

po.speak(polly, text, voice='Brian')

translated_text = tr.translateText(translate, text, language, 'es')
print translated_text
po.speak(polly, translated_text, voice='Enrique')

translated_text = tr.translateText(translate, text, language, 'pt')
print translated_text
po.speak(polly, translated_text, voice='Cristiano')

translated_text = tr.translateText(translate, text, language, 'fr')
print translated_text
po.speak(polly, translated_text, voice='Mathieu')

translated_text = tr.translateText(translate, text, language, 'de')
print translated_text
po.speak(polly, translated_text, voice='Hans')
