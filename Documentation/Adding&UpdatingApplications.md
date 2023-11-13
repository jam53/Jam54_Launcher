# Adding And/Or Updating Applications
## Prerequisites
> Make sure you have sqlite3 installed on your system

- The project should already come with an existing sqlite3 database. If this isn't the case, navigate to the following folder: `\Jam54_Launcher\src\main\resources\com\jam54\jam54_launcher`

- Open WSL (not CMD) and run the following commands to create a new database:
    - > We use WSL since the sqlite3 binary on Windows only supports ASCII characters.  
      > Working with the database in CMD (i.e. Windows binary) would cause issues since the database will contain none ASCII characters.
-   ```
    sqlite3 applications.sqlite
    ```

    ```sql
    CREATE TABLE applications(id INT PRIMARY KEY, name TEXT, logo TEXT, android INT, web INT, windows INT, releaseDate INT, latestUpdate INT, isGame INT);

    CREATE TABLE application_description(language TEXT, id INT REFERENCES applications(id), description TEXT, PRIMARY KEY (language, id));
    ```

<br>

## Adding an application
> After adding a new application, the `Jam54_Launcher.jar` and `Jam54LauncherSetup.msi` files will need to be rebuild and reuploaded. 
> Follow the steps described in [Updating the Jam54Launcher](./UpdatingTheJam54Launcher.md) to rebuild and upload the launcher's binaries.

We will need to insert a tuple in the database `applications.sqlite` that comes with the launcher, and also insert two lines in the `applicationsVersions.properties` file that is stored online. The former contains details about the application, the latter is used to check whether or not the application in question has a new update.

### applications.sqlite

You can add a new application by entering the following command in WSL (not CMD)
```sql
sqlite3 applications.sqlite

INSERT INTO applications VALUES(
    <Unique identifier of the app, this id will also be used in the launcher>,
    <Name of the app>,
    <Path to the logo of the app>,
    <If the app is available on android, then 1, else 0>,
    <If the app is available on the web, then 1, else 0>,
    <If the app is available on windows, then 1, else 0>,
    <The release date of the app in UNIX time (milliseconds)>,
    <The last time this app was updated in UNIX time (milliseconds)>,
    <If the app is a game, then 1, else 0>);

INSERT INTO application_description VALUES(
    <The language the description is written in>,
    <Unique identifier of the app this description is meant for>,
    <The description of the app>);
```

<details>
<summary>The following tuples are stored inside the database of the launcher</summary>

```
sqlite3 applications.sqlite
```

```sql
INSERT INTO applications VALUES(0, 'Stelexo', 'com/jam54/jam54_launcher/img/applicationLogos/Stelexo.png', 0, 0, 1, 1530835200, 1684925160, 1);

INSERT INTO applications VALUES(1, 'IToW', 'com/jam54/jam54_launcher/img/applicationLogos/IToW.png', 0, 0, 1, 1533340800, 1533340800, 0);

INSERT INTO applications VALUES(2, 'WToI', 'com/jam54/jam54_launcher/img/applicationLogos/WToI.png', 0, 0, 1, 1533340800, 1533340800, 0);

INSERT INTO applications VALUES(3, 'DGCTimer', 'com/jam54/jam54_launcher/img/applicationLogos/DGCTimer.png', 0, 0, 1, 1534118400, 1534291200, 0);

INSERT INTO applications VALUES(4, 'Smash&Fly', 'com/jam54/jam54_launcher/img/applicationLogos/Smash&Fly.png', 1, 0, 0, 1567987200, 1574643600, 1);

INSERT INTO applications VALUES(5, 'AutoEditor', 'com/jam54/jam54_launcher/img/applicationLogos/AutoEditor.jpg', 0, 0, 1, 1564876800, 1616029200, 0);

INSERT INTO applications VALUES(6, 'ImageSearcher', 'com/jam54/jam54_launcher/img/applicationLogos/ImageSearcher.png', 0, 0, 1, 1582765200, 1587168000, 0);

INSERT INTO applications VALUES(7, 'AstroRun', 'com/jam54/jam54_launcher/img/applicationLogos/AstroRun.png', 1, 0, 0, 1589500800, 1691575413, 1);

INSERT INTO applications VALUES(8, 'Flash', 'com/jam54/jam54_launcher/img/applicationLogos/Flash.png', 0, 0, 1, 1649808000, 1653004800, 0);

INSERT INTO applications VALUES(9, 'Sky Screenshot Stats', 'com/jam54/jam54_launcher/img/applicationLogos/SkyScreenshotStats.jpg', 0, 1, 0, 1654128000, 1657238400, 0);

INSERT INTO applications VALUES(10, 'Takma', 'com/jam54/jam54_launcher/img/applicationLogos/Takma.png', 0, 0, 1, 1692715543, 1699901910, 0);



INSERT INTO application_description VALUES('AR', 0, "ستيليكسو هو مجاني للعب لعبة صندوق الرمل للاعب واحد. يتميز بعالم تم إنشاؤه بشكل عشوائي تمامًا ، والذي يشمل الصخور والأشجار والحيوانات والأعداء وأكثر من ذلك بكثير! عالم مليء بالأسرار والمغامرات في انتظارك!\nبدأ تطوير Stelexo بشكل أساسي في يوليو 2018 وكان أول لعبة رئيسية عملت عليها.\nكانت الخطة هي إنشاء عالم تم إنشاؤه إجرائيًا ، معزز بالمخلوقات والأشياء وأكثر من ذلك.  أدركت أن نطاق هذا المشروع كان طريقة ما يمكنني القيام به بمفردي. لذلك بعد التحديث النهائي في ديسمبر 2020 ، انتهى تطوير Stelexo.");
INSERT INTO application_description VALUES('DE', 0, "Stelexo ist ein kostenloses Einzelspieler-Sandbox-Spiel. Mit einer völlig zufällig generierten Welt, die Steine, Bäume, Tiere, Feinde und vieles mehr umfasst! Eine Welt voller Geheimnisse und Abenteuer erwartet dich!\nDie Entwicklung von Stelexo begann hauptsächlich im Juli 2018 und war das erste große Spiel, an dem ich gearbeitet habe.\nDer Plan war, eine Welt zu schaffen, die prozedural generiert, mit Kreaturen, Objekten und mehr erweitert wurde.  Ich erkannte, dass der Umfang dieses Projekts weitaus besser war als das, was ich alleine tun konnte. Nach einem abschließenden Update im Dezember 2020 endete die Entwicklung von Stelexo.");
INSERT INTO application_description VALUES('EN', 0, "Stelexo is a free to play single player sandbox game. Featuring a completely randomly generated world, which includes rocks, trees, animals, enemies and much more! A world full of secrets and adventures awaits you!\nThe development of Stelexo started primarily in July of 2018 and was the first major game I worked on.\nThe plan was to create a world that was procedurally generated, augmented with creatures, objects and more.  I realised that this project's scope was way behond what I could do on my own. So after a final update in December 2020, the development of Stelexo ended.");
INSERT INTO application_description VALUES('ES', 0, "Stelexo es un juego sandbox gratuito para un solo jugador. Con un mundo completamente generado al azar, que incluye rocas, árboles, animales, enemigos y mucho más. Un mundo lleno de secretos y aventuras te espera!\nEl desarrollo de Stelexo comenzó principalmente en julio de 2018 y fue el primer juego importante en el que trabajé.\nEl plan era crear un mundo generado proceduralmente, aumentado con criaturas, objetos y más.  Me di cuenta de que el alcance de este proyecto iba mucho más allá de lo que podía hacer por mi cuenta. Así, tras una última actualización en diciembre de 2020, el desarrollo de Stelexo terminó.");
INSERT INTO application_description VALUES('ET', 0, "Stelexo on tasuta ühe mängijaga liivakastimäng. Sisaldab täiesti juhuslikult loodud maailma, mis sisaldab kive, puid, loomi, vaenlasi ja palju muud! Sind ootab ees maailm täis saladusi ja seiklusi!\nStelexo arendus algas peamiselt 2018. aasta juulis ja see oli esimene suurem mäng, mille kallal töötasin.\nPlaan oli luua protseduuriliselt genereeritud maailm, mida täiendati olendite, objektide ja rohkem.  Sain aru, et selle projekti ulatus oli palju suurem sellest, mida ma üksi teha saan. Nii et pärast viimast värskendust 2020. aasta detsembris Stelexo arendus lõppes.");
INSERT INTO application_description VALUES('FR', 0, "Stelexo est un jeu gratuit de bac à sable pour un seul joueur. Avec un monde généré de manière totalement aléatoire, qui comprend des rochers, des arbres, des animaux, des ennemis et bien plus encore ! Un monde plein de secrets et d'aventures vous attend !\nLe développement de Stelexo a commencé principalement en juillet 2018 et a été le premier jeu majeur sur lequel j'ai travaillé.\nLe plan était de créer un monde généré de manière procédurale, augmenté de créatures, d'objets et plus encore. Je me suis rendu compte que l'ampleur de ce projet dépassait de loin ce que je pouvais faire tout seul. Ainsi, après une dernière mise à jour en décembre 2020, le développement de Stelexo a pris fin.");
INSERT INTO application_description VALUES('HI', 0, "स्टेलेक्सो सिंगल प्लेयर सैंडबॉक्स गेम खेलने के लिए स्वतंत्र है। पूरी तरह से बेतरतीब ढंग से उत्पन्न दुनिया की विशेषता है, जिसमें चट्टानें, पेड़, जानवर, दुश्मन और बहुत कुछ शामिल हैं! रहस्यों और रोमांच से भरी एक दुनिया आपका इंतजार कर रही है!\nस्टेलेक्सो का विकास मुख्य रूप से 2018 के जुलाई में शुरू हुआ था और यह पहला प्रमुख गेम था जिस पर मैंने काम किया था।\nयोजना एक ऐसी दुनिया बनाने की थी जो जीवों, वस्तुओं और अधिक।  मुझे एहसास हुआ कि इस परियोजना का दायरा मैं अपने दम पर जो कुछ कर सकता था उससे कहीं अधिक था। इसलिए दिसंबर 2020 में अंतिम अपडेट के बाद, स्टेलेक्सो का विकास समाप्त हो गया।");
INSERT INTO application_description VALUES('ID', 0, "Stelexo adalah permainan kotak pasir pemain tunggal gratis untuk dimainkan. Menampilkan dunia yang dibuat secara acak, yang mencakup batu, pohon, hewan, musuh, dan banyak lagi! Dunia yang penuh rahasia dan petualangan menanti Anda!\nPengembangan Stelexo dimulai terutama pada Juli 2018 dan merupakan game besar pertama yang saya kerjakan.\nRencananya adalah menciptakan dunia yang dihasilkan secara prosedural, ditambah dengan makhluk, objek, dan lagi.  Saya menyadari bahwa cakupan proyek ini jauh melampaui apa yang dapat saya lakukan sendiri. Jadi setelah pembaruan terakhir pada Desember 2020, pengembangan Stelexo berakhir.");
INSERT INTO application_description VALUES('JA', 0, "Stelexoは、無料で遊べる一人用のサンドボックスゲームです。岩、木、動物、敵など、完全にランダムに生成される世界が特徴です。秘密と冒険に満ちた世界があなたを待っています\nStelexoの開発は主に2018年の7月に始まり、私が手掛けた最初の大型ゲームでした\nプロシージャルに生成され、クリーチャーやオブジェクトなどで拡張された世界を作る計画でした。このプロジェクトは、自分一人でできる範囲をはるかに超えていることに気づきました。そのため、2020年12月の最終アップデートを最後に、ステレコの開発は終了しました。");
INSERT INTO application_description VALUES('KO', 0, "Stelexo는 싱글 플레이어 샌드 박스 게임을 무료로 즐길 수 있습니다. 바위, 나무, 동물, 적 등을 포함하는 완전히 무작위로 생성 된 세계를 특징으로합니다! 비밀과 모험으로 가득 찬 세계가 여러분을 기다리고 있습니다!\nStelexo의 개발은 주로 2018년 7월에 시작되어 제가 작업한 첫 번째 주요 게임이었습니다.\n계획은 절차적으로 생성되고 생물, 물체 등으로 증강된 세계를 만드는 것이었습니다.  나는이 프로젝트의 범위가 내가 스스로 할 수있는 것을 방해한다는 것을 깨달았다. 그래서 2020 년 12 월 최종 업데이트 후 Stelexo의 개발이 끝났습니다.");
INSERT INTO application_description VALUES('NL', 0, "Stelexo is een gratis te spelen spel voor één speler. Met een volledig willekeurig gegenereerde wereld, met rotsen, bomen, dieren, vijanden en nog veel meer! Een wereld vol geheimen en avonturen wacht op je!\nDe ontwikkeling van Stelexo begon voornamelijk in juli van 2018 en was het eerste grote spel waar ik aan werkte.\nHet plan was om een wereld te creëren die procedureel gegenereerd was, uitgebreid met wezens, objecten en meer.  Ik besefte dat de omvang van dit project veel groter was dan wat ik alleen kon doen. Dus na een laatste update in december 2020 eindigde de ontwikkeling van Stelexo.");
INSERT INTO application_description VALUES('PT', 0, "Estelosxo é um jogo de sandbox de um único jogador. Apresentando um mundo completamente gerado aleatoriamente, que inclui rochas, árvores, animais, inimigos e muito mais! Um mundo cheio de segredos e aventuras espera por você!\nO desenvolvimento de Estelaxo começou principalmente em julho de 2018 e foi o primeiro grande jogo em que trabalhei.\nO plano era criar um mundo que fosse gerado processualmente, aumentado com criaturas, objetos e muito mais.  Eu percebi que o escopo deste projeto era muito bem honrado o que eu poderia fazer por conta própria. Assim, após uma atualização final em dezembro de 2020, o desenvolvimento de Estelaxo terminou.");
INSERT INTO application_description VALUES('RU', 0, "Stelexo - это бесплатная однопользовательская игра-песочница. Благодаря полностью случайно сгенерированному миру, который включает в себя камни, деревья, животных, врагов и многое другое! Мир, полный тайн и приключений, ждет вас!\nРазработка Stelexo началась в основном в июле 2018 года и стала первой крупной игрой, над которой я работал.\nПлан состоял в том, чтобы создать процедурно сгенерированный мир, дополненный существами, объектами и многим другим.  Я понял, что масштаб этого проекта намного превосходит то, что я мог бы сделать сам. Итак, после окончательного обновления в декабре 2020 года разработка Stelexo завершилась.");
INSERT INTO application_description VALUES('TR', 0, "Stelexo oynamak için ücretsiz tek oyunculu bir sandbox oyunudur. Kayaları, ağaçları, hayvanları, düşmanları ve çok daha fazlasını içeren tamamen rastgele oluşturulmuş bir dünyaya sahip! Sırlar ve maceralarla dolu bir dünya sizi bekliyor!\nStelexo'nun gelişimi öncelikle Temmuz 2018'de başladı ve üzerinde çalıştığım ilk büyük oyundu.\nPlan, prosedürel olarak üretilmiş, yaratıklar, nesneler ve daha fazlasıyla zenginleştirilmiş bir dünya yaratmaktı.  Bu projenin kapsamının, kendi başıma yapabileceklerimi çok iyi bildiğimi fark ettim. Böylece Aralık 2020'deki son güncellemeden sonra, Stelexo'nun gelişimi sona erdi.");
INSERT INTO application_description VALUES('ZH', 0, "Stelexo 是一款免费的单人沙盒游戏。拥有一个完全随机生成的世界，其中包括岩石、树木、动物、敌人等等！一个充满秘密和冒险的世界等着你！\nStelexo 的开发主要于 2018 年 7 月开始，是我参与的第一款大型游戏。\n计划是创建一个程序生成的世界，并增加了生物、物体和更多的。我意识到这个项目的范围远远超过了我自己可以做的事情。所以在 2020 年 12 月的最后一次更新之后，Stelexo 的开发就结束了。");

INSERT INTO application_description VALUES('AR', 1, "يتم استخدام IToW للتلاعب بسلسلة تم إنشاؤها بواسطة WToI ، مرة أخرى إلى شكلها الأصلي.");
INSERT INTO application_description VALUES('DE', 1, "IToW wird verwendet, um einen von WToI erstellten String zu manipulieren, zurück in seine ursprüngliche Form.");
INSERT INTO application_description VALUES('EN', 1, "IToW is used to manipulate a string created by WToI, back to it's original form.");
INSERT INTO application_description VALUES('ES', 1, "IToW se utiliza para manipular una cadena creada por WToI, de vuelta a su forma original.");
INSERT INTO application_description VALUES('ET', 1, "IToW-d kasutatakse WToI loodud stringi manipuleerimiseks, naases selle algsele kujule.");
INSERT INTO application_description VALUES('FR', 1, "IToW est utilisé pour manipuler une chaîne de caractères créée par WToI, pour la ramener à sa forme originale.");
INSERT INTO application_description VALUES('HI', 1, "IToW का उपयोग WToI द्वारा बनाई गई स्ट्रिंग में हेरफेर करने के लिए किया जाता है, वापस अपने मूल रूप में।");
INSERT INTO application_description VALUES('ID', 1, "IToW digunakan untuk memanipulasi string yang dibuat oleh WToI, kembali ke bentuk aslinya.");
INSERT INTO application_description VALUES('JA', 1, "IToWは、WToIで作成した文字列を元の形に操作するために使用します。");
INSERT INTO application_description VALUES('KO', 1, "IToW는 WToI에 의해 생성 된 문자열을 조작하는 데 사용되며 원래 형식으로 돌아갑니다.");
INSERT INTO application_description VALUES('NL', 1, "IToW wordt gebruikt om een door WToI gemaakte string terug te brengen in zijn oorspronkelijke vorm.");
INSERT INTO application_description VALUES('PT', 1, "IToW é usado para manipular uma string criada pela WToI, de volta à sua forma original.");
INSERT INTO application_description VALUES('RU', 1, "IToW используется для управления строкой, созданной WToI, возвращая ее в исходную форму.");
INSERT INTO application_description VALUES('TR', 1, "IToW, WToI tarafından oluşturulan bir dizeyi orijinal biçimine geri döndürmek için kullanılır.");
INSERT INTO application_description VALUES('ZH', 1, "IToW 用于操作由 WToI 创建的字符串，使其恢复为原始形式。");

INSERT INTO application_description VALUES('AR', 2, "يستخدم WToI للتلاعب بسلسلة ، والتي يمكن بعد ذلك التخلص منها بواسطة WToI مرة أخرى إلى شكلها الأصلي.");
INSERT INTO application_description VALUES('DE', 2, "WToI wird verwendet, um einen String zu manipulieren, der dann von WToI wieder in seine ursprüngliche Form entmanipuliert werden kann.");
INSERT INTO application_description VALUES('EN', 2, "WToI is used to manipulate a string, that can then be demanipulated by WToI back to it's original form.");
INSERT INTO application_description VALUES('ES', 2, "WToI se utiliza para manipular una cadena, que luego puede ser desmanipulada por WToI de vuelta a su forma original.");
INSERT INTO application_description VALUES('ET', 2, "WToI-d kasutatakse stringi manipuleerimiseks, mille saab seejärel WToI-ga demanipuleerida tagasi algsele kujule.");
INSERT INTO application_description VALUES('FR', 2, "WToI est utilisé pour manipuler une chaîne de caractères, qui peut ensuite être démanipulée par WToI pour retrouver sa forme originale.");
INSERT INTO application_description VALUES('HI', 2, "डब्ल्यूटीओआई का उपयोग एक स्ट्रिंग में हेरफेर करने के लिए किया जाता है, जिसे तब डब्ल्यूटीओआई द्वारा अपने मूल रूप में वापस लाया जा सकता है।");
INSERT INTO application_description VALUES('ID', 2, "WToI digunakan untuk memanipulasi string, yang kemudian dapat dimanipulasi oleh WToI kembali ke bentuk aslinya.");
INSERT INTO application_description VALUES('JA', 2, "WToIは文字列を操作するために使用され、その後WToIによって元の形に戻すことができます。");
INSERT INTO application_description VALUES('KO', 2, "WToI는 문자열을 조작하는 데 사용되며, WToI에 의해 원래 형식으로 다시 조작 될 수 있습니다.");
INSERT INTO application_description VALUES('NL', 2, "WToI wordt gebruikt om een string te manipuleren, die dan door WToI kan worden gedemanipuleerd tot zijn oorspronkelijke vorm.");
INSERT INTO application_description VALUES('PT', 2, "WToI é usado para manipular uma corda, que pode então ser demanipulada pela WToI de volta à sua forma original.");
INSERT INTO application_description VALUES('RU', 2, "WToI используется для манипулирования строкой, которая затем может быть деманипулирована WToI обратно в исходную форму.");
INSERT INTO application_description VALUES('TR', 2, "WToI, bir dizeyi manipüle etmek için kullanılır, bu dize daha sonra WToI tarafından orijinal biçimine geri döndürülebilir.");
INSERT INTO application_description VALUES('ZH', 2, "WToI 用于操作字符串，然后可以通过 WToI 将其解除操作返回到它的原始形式。");

INSERT INTO application_description VALUES('AR', 3, "DGCTimer ، مكتوب بـ C# ويتم العد التنازلي من كمية معينة من الدقائق والثواني. كما أنه قادر على تشغيل الصوت عند انتهاء المؤقت.");
INSERT INTO application_description VALUES('DE', 3, "DGCTimer, wird in C# geschrieben und zählt von einer bestimmten Anzahl von Minuten und Sekunden herunter. Es ist auch in der Lage, einen Ton zu spielen, wenn der Timer endet.");
INSERT INTO application_description VALUES('EN', 3, "DGCTimer, is written in C# and counts down from a given amount of minutes and seconds. It is also able to play a sound when the timer ends.");
INSERT INTO application_description VALUES('ES', 3, "DGCTimer, está escrito en C# y realiza una cuenta atrás a partir de una cantidad determinada de minutos y segundos. También es capaz de reproducir un sonido cuando el temporizador termina.");
INSERT INTO application_description VALUES('ET', 3, "DGCTimer on kirjutatud C# keeles ja loendab etteantud arvust minutitest ja sekunditest. Samuti on see võimeline esitama heli, kui taimer lõpeb.");
INSERT INTO application_description VALUES('FR', 3, "DGCTimer, est écrit en C# et effectue un compte à rebours à partir d'un nombre donné de minutes et de secondes. Il est également capable d'émettre un son lorsque la minuterie se termine.");
INSERT INTO application_description VALUES('HI', 3, "DGCTimer, C# में लिखा गया है और दिए गए मिनटों और सेकंडों से गिना जाता है। टाइमर समाप्त होने पर यह ध्वनि चलाने में भी सक्षम है।");
INSERT INTO application_description VALUES('ID', 3, "DGCTimer, ditulis dalam C# dan menghitung mundur dari jumlah menit dan detik tertentu. Hal ini juga dapat memainkan suara ketika timer berakhir.");
INSERT INTO application_description VALUES('JA', 3, "DGCTimerは、C#で書かれており、与えられた分と秒からカウントダウンしていきます。また、タイマー終了時にサウンドを再生することも可能です。");
INSERT INTO application_description VALUES('KO', 3, "DGCTimer는 C #으로 작성되었으며 주어진 분 및 초 단위에서 카운트 다운됩니다. 타이머가 끝나면 소리를 재생할 수도 있습니다.");
INSERT INTO application_description VALUES('NL', 3, "DGCTimer, is geschreven in C# en telt af vanaf een bepaald aantal minuten en seconden. Hij kan ook een geluid afspelen wanneer de timer afloopt.");
INSERT INTO application_description VALUES('PT', 3, "DGCTimer, está escrito em C# e conta a partir de uma determinada quantidade de minutos e segundos. Ele também é capaz de reproduzir um som quando o temporizador termina.");
INSERT INTO application_description VALUES('RU', 3, "DGCTimer, написан на C# и ведет обратный отсчет от заданного количества минут и секунд. Он также способен воспроизводить звук, когда таймер заканчивается.");
INSERT INTO application_description VALUES('TR', 3, "DGCTimer, C# ile yazılır ve belirli bir dakika ve saniye miktarından geri sayar. Ayrıca zamanlayıcı sona erdiğinde bir ses çalabilir.");
INSERT INTO application_description VALUES('ZH', 3, "DGCTimer 是用 C# 编写的，从给定的分钟和秒数开始倒计时。它还可以在计时器结束时播放声音。");

INSERT INTO application_description VALUES('AR', 4, "لعبة سماش آند فلاي هي لعبة حول قيادة طائرة عبر عوالم مختلفة مستوحاة من بيولوجيات العالم الحقيقي. حيث تحتاج إلى مراوغة واطلاق النار طريقك من خلال المستويات.");
INSERT INTO application_description VALUES('DE', 4, "Smash&Fly ist ein Spiel über das Fliegen eines Luftschiffs durch verschiedene Welten, inspiriert von realen Weltbiomen. Wo Sie ausweichen und schießen Sie Ihren Weg durch die Ebenen müssen.");
INSERT INTO application_description VALUES('EN', 4, "Smash&Fly is a game about flying an airship through different worlds inspired by real world biomes. Where you need to dodge and shoot your way through the levels.");
INSERT INTO application_description VALUES('ES', 4, "Smash&Fly es un juego que consiste en pilotar una aeronave a través de diferentes mundos inspirados en los biomas del mundo real. Donde tienes que esquivar y disparar para atravesar los niveles.");
INSERT INTO application_description VALUES('ET', 4, "Smash&Fly on mäng õhulaeva lendamisest läbi erinevate maailmade, mis on inspireeritud pärismaailma elustikust. Kus peate põigelda ja tulistada end läbi tasemete.");
INSERT INTO application_description VALUES('FR', 4, "Smash&Fly est un jeu qui consiste à piloter un dirigeable à travers différents mondes inspirés de biomes réels. Vous devez esquiver et tirer pour vous frayer un chemin à travers les niveaux.");
INSERT INTO application_description VALUES('HI', 4, "स्मैश एंड फ्लाई वास्तविक दुनिया के बायोम से प्रेरित विभिन्न दुनिया के माध्यम से एक हवाई पोत को उड़ाने के बारे में एक खेल है। जहां आपको चकमा देने और स्तरों के माध्यम से अपना रास्ता शूट करने की आवश्यकता है।");
INSERT INTO application_description VALUES('ID', 4, "Smash&Fly adalah gim tentang menerbangkan pesawat melalui dunia berbeda yang terinspirasi oleh bioma dunia nyata. Di mana Anda harus menghindar dan menembak melalui level.");
INSERT INTO application_description VALUES('JA', 4, "Smash&Flyは、現実世界のバイオームをモチーフにしたさまざまな世界を飛行船で駆け抜けるゲームです。あなたはレベルを通してあなたの方法をかわすと撃つ必要があるところ。");
INSERT INTO application_description VALUES('KO', 4, "Smash & Fly는 실제 생물 군계에서 영감을 얻은 다양한 세계를 통해 비행선을 비행하는 게임입니다. 레벨을 피하고 쏘아야하는 곳.");
INSERT INTO application_description VALUES('NL', 4, "Smash&Fly is een spel over het vliegen van een luchtschip door verschillende werelden, geïnspireerd door echte wereld biomen. Waar je moet ontwijken en je een weg door de levels moet schieten.");
INSERT INTO application_description VALUES('PT', 4, "Smash&Fly é um jogo sobre voar um dirigível através de diferentes mundos inspirados em biomas do mundo real. Onde você precisa se esquivar e atirar seu caminho através dos níveis.");
INSERT INTO application_description VALUES('RU', 4, "Smash&Fly - это игра о полете на дирижабле по разным мирам, вдохновленная биомами реального мира. Где вам нужно уворачиваться и пробивать себе путь через уровни.");
INSERT INTO application_description VALUES('TR', 4, "Smash & Fly, gerçek dünya biyomlarından esinlenerek farklı dünyalarda bir hava gemisini uçurmakla ilgili bir oyundur. Seviyeler boyunca atlatmanız ve ateş etmeniz gereken yer.");
INSERT INTO application_description VALUES('ZH', 4, "Smash&Fly 是一款关于驾驶飞艇穿越不同世界的游戏，灵感来自现实世界的生物群系。您需要在关卡中躲避和射击的地方。");

INSERT INTO application_description VALUES('AR', 5, "AutoEditor ، مكتوب في C# ويقوم بتحرير مقاطع الفيديو تلقائيًا ، باستخدام اكتشاف الصور.");
INSERT INTO application_description VALUES('DE', 5, "AutoEditor, wird in C# geschrieben und bearbeitet Videos automatisch mithilfe der Bilderkennung.");
INSERT INTO application_description VALUES('EN', 5, "AutoEditor, is written in C# and automatically edits videos, using image detection.");
INSERT INTO application_description VALUES('ES', 5, "AutoEditor, está escrito en C# y edita automáticamente los vídeos, utilizando la detección de imágenes.");
INSERT INTO application_description VALUES('ET', 5, "AutoEditor on kirjutatud C# keeles ja redigeerib automaatselt videoid, kasutades pildituvastust.");
INSERT INTO application_description VALUES('FR', 5, "AutoEditor, écrit en C#, édite automatiquement les vidéos en utilisant la détection d'images.");
INSERT INTO application_description VALUES('HI', 5, "AutoEditor, C# में लिखा गया है और इमेज डिटेक्शन का उपयोग करके स्वचालित रूप से वीडियो संपादित करता है।");
INSERT INTO application_description VALUES('ID', 5, "AutoEditor, ditulis dalam C# dan secara otomatis mengedit video, menggunakan deteksi gambar.");
INSERT INTO application_description VALUES('JA', 5, "AutoEditorは、C#で書かれており、画像検出を利用して自動的にビデオを編集します。");
INSERT INTO application_description VALUES('KO', 5, "자동 편집기는 C #으로 작성되었으며 이미지 감지를 사용하여 비디오를 자동으로 편집합니다.");
INSERT INTO application_description VALUES('NL', 5, "AutoEditor, is geschreven in C# en bewerkt video's automatisch, met behulp van beelddetectie.");
INSERT INTO application_description VALUES('PT', 5, "AutoEditor, é escrito em C# e edita automaticamente vídeos, usando detecção de imagem.");
INSERT INTO application_description VALUES('RU', 5, "Авторедактор, написан на C# и автоматически редактирует видео, используя обнаружение изображений.");
INSERT INTO application_description VALUES('TR', 5, "Otomatik Düzenleyici, C# ile yazılmıştır ve görüntü algılamayı kullanarak videoları otomatik olarak düzenler.");
INSERT INTO application_description VALUES('ZH', 5, "AutoEditor 是用 C# 编写的，使用图像检测自动编辑视频。");

INSERT INTO application_description VALUES('AR', 6, "تتم كتابة ImageSearcher في C# ويبحث عن صورة معينة داخل مقطع فيديو. بمجرد العثور على الصورة ، يقوم البرنامج بإخراج الطوابع الزمنية التي تم العثور على الصورة بها.");
INSERT INTO application_description VALUES('DE', 6, "ImageSearcher wird in C# geschrieben und sucht nach einem bestimmten Bild innerhalb eines Videos. Sobald das Bild gefunden wurde, gibt das Programm die Zeitstempel aus, bei denen das Bild gefunden wurde.");
INSERT INTO application_description VALUES('EN', 6, "ImageSearcher is written in C# and searches for a certain image inside a video. Once the image is found, the program outputs the timestamps at which the image was found.");
INSERT INTO application_description VALUES('ES', 6, "ImageSearcher está escrito en C# y busca una determinada imagen dentro de un vídeo. Una vez encontrada la imagen, el programa emite las marcas de tiempo en las que se encontró la imagen.");
INSERT INTO application_description VALUES('ET', 6, "ImageSearcher on kirjutatud C# keeles ja otsib video seest teatud pilti. Kui pilt on leitud, väljastab programm ajatemplid, millal pilt leiti.");
INSERT INTO application_description VALUES('FR', 6, "ImageSearcher est écrit en C# et recherche une certaine image dans une vidéo. Une fois l'image trouvée, le programme sort les horodatages auxquels l'image a été trouvée.");
INSERT INTO application_description VALUES('HI', 6, "ImageSearcher C# में लिखा गया है और एक वीडियो के अंदर एक निश्चित छवि की खोज करता है। एक बार छवि मिल जाने के बाद, प्रोग्राम उस टाइमस्टैम्प को आउटपुट करता है जिस पर छवि मिली थी।");
INSERT INTO application_description VALUES('ID', 6, "ImageSearcher ditulis dalam C# dan mencari gambar tertentu di dalam video. Setelah gambar ditemukan, program mengeluarkan stempel waktu di mana gambar ditemukan.");
INSERT INTO application_description VALUES('JA', 6, "ImageSearcherはC#で記述され、ビデオ内の特定の画像を検索します。画像が見つかったら、その画像が見つかった時のタイムスタンプを出力するプログラムです。");
INSERT INTO application_description VALUES('KO', 6, "ImageSearcher는 C#으로 작성되었으며 비디오 내에서 특정 이미지를 검색합니다. 이미지가 발견되면 프로그램은 이미지가 발견 된 타임 스탬프를 출력합니다.");
INSERT INTO application_description VALUES('NL', 6, "ImageSearcher is geschreven in C# en zoekt naar een bepaalde afbeelding in een video. Zodra de afbeelding is gevonden, geeft het programma de tijdstippen weer waarop de afbeelding is gevonden.");
INSERT INTO application_description VALUES('PT', 6, "ImageSearcher é escrito em C# e procura por uma determinada imagem dentro de um vídeo. Uma vez que a imagem é encontrada, o programa produz os horários em que a imagem foi encontrada.");
INSERT INTO application_description VALUES('RU', 6, "ImageSearcher написан на C# и выполняет поиск определенного изображения внутри видео. Как только изображение найдено, программа выводит временные метки, в которые было найдено изображение.");
INSERT INTO application_description VALUES('TR', 6, "ImageSearcher C# ile yazılmıştır ve bir videonun içinde belirli bir resmi arar. Görüntü bulunduğunda, program görüntünün bulunduğu zaman damgalarını çıkarır.");
INSERT INTO application_description VALUES('ZH', 6, "ImageSearcher 是用 C# 编写的，用于搜索视频中的特定图像。找到图像后，程序会输出找到图像的时间戳。");

INSERT INTO application_description VALUES('AR', 7, "AstroRun هي لعبة مستقلة طورها فريق صغير من ثلاثة أشخاص. إنه عن مغامر مفقود في الفضاء ، والذي يجب أن يكمل مستويات مختلفة والتي هي جميعها جزء من مجموعة متنوعة من العوالم.\nيدور كل عالم حول موضوع معين وله أجسامه الفريدة المستعصية على الحل مثل الترامبولين والمسامير المتساقطة والأعداء والأبراج وأكثر من ذلك بكثير. على طول الطريق ، يمكن للمغامر جمع العديد من العناصر التي يمكن استخدامها لشراء عبوات وملحقات جديدة.");
INSERT INTO application_description VALUES('DE', 7, "AstroRun ist ein Indie-Spiel von einem kleinen Team von drei Personen entwickelt. Es geht um einen verlorenen Abenteurer im Weltraum, der verschiedene Ebenen abschließen muss, die alle Teil einer vielfältigen Sammlung von Welten sind.\nJede Welt basiert auf einem bestimmten Thema und hat ihre eigenen unwiderstehlichen Objekte wie Trampoline, fallende Spitzen, Feinde, Türmchen und vieles mehr. Auf dem Weg kann der Abenteurer verschiedene Gegenstände sammeln, die zum Kauf neuer Pakete und Accessoires verwendet werden können.");
INSERT INTO application_description VALUES('EN', 7, "AstroRun is an indie game developed by a small team of three people. It's about a lost adventurer in space, who must complete various levels that are all part of a diverse collection of worlds.\nEach world is based around a certain theme and has its own unique intractable objects such as trampolines, falling spikes, enemies, turrets and much more. Along the way, the adventurer can collect various items which can be used to purchase new packages and accessories.");
INSERT INTO application_description VALUES('ES', 7, "AstroRun es un juego independiente desarrollado por un pequeño equipo de tres personas. Se trata de un aventurero perdido en el espacio, que debe completar varios niveles que forman parte de una variada colección de mundos.\nCada mundo se basa en un tema determinado y tiene sus propios objetos intratables como trampolines, picos que caen, enemigos, torretas y mucho más. A lo largo del camino, el aventurero puede recoger diversos objetos que pueden utilizarse para comprar nuevos paquetes y accesorios.");
INSERT INTO application_description VALUES('ET', 7, "AstroRun on indie-mäng, mille on välja töötanud väike kolmeliikmeline meeskond. See räägib kosmoses eksinud seiklejast, kes peab läbima erinevaid tasemeid, mis kõik on osa erinevatest maailmade kogumist.\nIga maailm põhineb teatud teemal ja sellel on oma ainulaadsed lahendamatud objektid, nagu batuudid, langevad naelu, vaenlased, tornid ja palju muud. Teekonnal saab seikleja koguda erinevaid esemeid, mida saab kasutada uute pakendite ja tarvikute ostmiseks.");
INSERT INTO application_description VALUES('FR', 7, "AstroRun est un jeu indépendant développé par une petite équipe de trois personnes. Il s'agit d'un aventurier perdu dans l'espace, qui doit franchir différents niveaux qui font tous partie d'une collection de mondes divers.\nChaque monde est basé sur un certain thème et possède ses propres objets insolubles tels que des trampolines, des piques, des ennemis, des tourelles et bien plus encore. En cours de route, l'aventurier peut collecter divers objets qui peuvent être utilisés pour acheter de nouveaux paquets et accessoires.");
INSERT INTO application_description VALUES('HI', 7, "एस्ट्रोरन एक इंडी गेम है जिसे तीन लोगों की एक छोटी टीम द्वारा विकसित किया गया है। यह अंतरिक्ष में एक खोए हुए साहसी के बारे में है, जिसे विभिन्न स्तरों को पूरा करना होगा जो दुनिया के विविध संग्रह का हिस्सा हैं।\nप्रत्येक दुनिया एक निश्चित विषय पर आधारित है और इसकी अपनी अनूठी अट्रैक्टिव वस्तुएं हैं जैसे कि ट्रैम्पोलिन, गिरने वाले स्पाइक्स, दुश्मन, बुर्ज और भी बहुत कुछ। रास्ते में, साहसी विभिन्न वस्तुओं को एकत्र कर सकता है जिनका उपयोग नए पैकेज और सहायक उपकरण खरीदने के लिए किया जा सकता है।");
INSERT INTO application_description VALUES('ID', 7, "AstroRun adalah game indie yang dikembangkan oleh tim kecil yang terdiri dari tiga orang. Ini tentang seorang petualang yang tersesat di luar angkasa, yang harus menyelesaikan berbagai level yang semuanya merupakan bagian dari koleksi dunia yang beragam.\nSetiap dunia didasarkan pada tema tertentu dan memiliki objek unik yang sulit dipecahkan seperti trampolin, paku jatuh, musuh, menara dan banyak lagi. Sepanjang jalan, petualang dapat mengumpulkan berbagai item yang dapat digunakan untuk membeli paket dan aksesoris baru.");
INSERT INTO application_description VALUES('JA', 7, "AstroRunは、3人の小さなチームで開発されたインディーゲームです。宇宙で迷子になった冒険家が、さまざまなワールドをクリアしていくお話です。\n各ワールドにはテーマがあり、トランポリンや落下スパイク、敵、タレットなど、独特の難物が出てきます。冒険者は道中でさまざまなアイテムを集め、新しいパッケージやアクセサリーを購入することができます。");
INSERT INTO application_description VALUES('KO', 7, "AstroRun은 세 명으로 구성된 소규모 팀이 개발 한 인디 게임입니다. 그것은 우주에서 잃어버린 모험가에 관한 것으로, 다양한 세계 컬렉션의 일부인 다양한 레벨을 완료해야합니다.\n각 세계는 특정 주제를 기반으로하며 트램폴린, 떨어지는 스파이크, 적, 포탑 등과 같은 고유 한 다루기 어려운 물체를 가지고 있습니다. 길을 따라 모험가는 새로운 패키지와 액세서리를 구입하는 데 사용할 수있는 다양한 아이템을 수집 할 수 있습니다.");
INSERT INTO application_description VALUES('NL', 7, "AstroRun is een indiegame ontwikkeld door een klein team van drie mensen. Het gaat over een verdwaalde avonturier in de ruimte, die verschillende levels moet voltooien die allemaal deel uitmaken van een diverse verzameling werelden.\nElke wereld is gebaseerd op een bepaald thema en heeft zijn eigen unieke hardnekkige objecten zoals trampolines, vallende spikes, vijanden, torentjes en nog veel meer. Onderweg kan de avonturier verschillende voorwerpen verzamelen waarmee nieuwe pakketten en accessoires kunnen worden gekocht.");
INSERT INTO application_description VALUES('PT', 7, "AstroRun é um jogo indie desenvolvido por uma pequena equipe de três pessoas. Trata-se de um aventureiro perdido no espaço, que deve completar vários níveis que fazem parte de uma coleção diversificada de mundos.\nCada mundo é baseado em um determinado tema e tem seus próprios objetos intratáveis únicos, como trampolins, espinhos caindo, inimigos, torres e muito mais. Ao longo do caminho, o aventureiro pode coletar vários itens que podem ser usados para comprar novos pacotes e acessórios.");
INSERT INTO application_description VALUES('RU', 7, "AstroRun - это инди-игра, разработанная небольшой командой из трех человек. Речь идет о затерянном в космосе искателе приключений, который должен пройти различные уровни, являющиеся частью разнообразной коллекции миров.\n Каждый мир основан на определенной теме и имеет свои собственные уникальные неразрешимые объекты, такие как батуты, падающие шипы, враги, башни и многое другое. По пути искатель приключений может собирать различные предметы, которые можно использовать для покупки новых комплектов и аксессуаров.");
INSERT INTO application_description VALUES('TR', 7, "AstroRun, üç kişilik küçük bir ekip tarafından geliştirilen bağımsız bir oyundur. Bu, hepsi farklı bir dünya koleksiyonunun parçası olan çeşitli seviyeleri tamamlaması gereken uzayda kayıp bir maceraperest hakkındadır.\nHer dünya belirli bir temaya dayanır ve trambolinler, düşen sivri uçlar, düşmanlar, taretler ve çok daha fazlası gibi kendine özgü inatçı nesnelere sahiptir. Yol boyunca, maceracı yeni paketler ve aksesuarlar satın almak için kullanılabilecek çeşitli eşyalar toplayabilir.");
INSERT INTO application_description VALUES('ZH', 7, "AstroRun 是一款由三人小团队开发的独立游戏。它是关于一个迷失在太空中的冒险家，他必须完成不同的关卡，这些关卡都是不同世界的一部分。\n每个世界都基于特定的主题，并且有自己独特的难以处理的物体，例如蹦床、坠落的尖刺、敌人、炮塔等等。一路上，冒险者可以收集各种物品，用于购买新的包裹和配件。");

INSERT INTO application_description VALUES('AR', 8, "برنامج Flash هو برنامج يسمح للمرء بإنشاء اختبارات صغيرة. حيث يمكن للمستخدم اختيار الأسئلة من مجموعة متنوعة من القوالب الجاهزة.\nبما في ذلك على سبيل المثال لا الحصر :\n- أسئلة متعددة الاختيارات \n- أسئلة متعددة الإجابات \n- أسئلة مفتوحة \n- أسئلة مفتوحة  VALUES(أرقام فقط )\n- أسئلة صحيحة/خاطئة");
INSERT INTO application_description VALUES('DE', 8, "Flash ist ein Programm, mit dem man kleine Quizze erstellen kann. Wo der Benutzer Fragen aus einer Vielzahl von vorgefertigten Vorlagen auswählen kann.\nEinschließlich, aber nicht beschränkt auf:\n- Multiple-Choice-Fragen\n- Multiple Answer Questions\n- Offene Fragen\n- Offene Fragen  VALUES(nur Zahlen)\n- Richtige/Falsche Fragen");
INSERT INTO application_description VALUES('EN', 8, "Flash is a program that allows one to create small quizzes. Where the user can choose questions from a variety of ready-made templates.\nIncluding but not limited to:\n- Multiple Choice Questions\n- Multiple Answer Questions\n- Open Questions\n- Open Questions  VALUES(numbers only)\n- True/False Questions");
INSERT INTO application_description VALUES('ES', 8, "Flash es un programa que permite crear pequeños cuestionarios. Donde el usuario puede elegir preguntas de una variedad de plantillas ya hechas.\nIncluyendo pero no limitado a:\n- Preguntas de opción múltiple\n- Preguntas de respuesta múltiple\n- Preguntas abiertas\n- Preguntas abiertas  VALUES(sólo números)\n- Preguntas de verdadero/falso");
INSERT INTO application_description VALUES('ET', 8, "Flash on programm, mis võimaldab luua väikeseid viktoriini. kus kasutaja saab valida küsimusi mitmesuguste valmismallide hulgast.\nSealhulgas, kuid mitte ainult:\n- valikvastustega küsimused\n- mitme vastusega küsimused\n- avatud küsimused\n- avatud küsimused  VALUES(ainult numbrid)\n- Õiged/valed küsimused");
INSERT INTO application_description VALUES('FR', 8, "Flash est un programme qui permet de créer de petits quizz. L'utilisateur peut choisir des questions à partir d'une variété de modèles prêts à l'emploi, y compris, mais sans s'y limiter, des questions à choix multiples, des questions à réponses multiples, des questions ouvertes, des questions ouvertes  VALUES(chiffres uniquement), des questions vrai-faux.");
INSERT INTO application_description VALUES('HI', 8, "फ्लैश एक ऐसा प्रोग्राम है जो व्यक्ति को छोटी प्रश्नोत्तरी बनाने की अनुमति देता है। जहां उपयोगकर्ता विभिन्न प्रकार के तैयार किए गए टेम्प्लेट से प्रश्न चुन सकता है।\nसहित लेकिन इन्हीं तक सीमित नहीं:\n- बहुविकल्पीय प्रश्न\n- एकाधिक उत्तर प्रश्न\n- खुले प्रश्न\n- खुले प्रश्न  VALUES(केवल संख्याएं)\n- सही/गलत प्रश्न");
INSERT INTO application_description VALUES('ID', 8, "Flash adalah program yang memungkinkan seseorang untuk membuat kuis kecil. Di mana pengguna dapat memilih pertanyaan dari berbagai template siap pakai.\nTermasuk namun tidak terbatas pada:\n- Pertanyaan Pilihan Ganda\n- Pertanyaan Jawaban Ganda\n- Pertanyaan Terbuka\n- Pertanyaan Terbuka  VALUES(hanya angka)\n- Pertanyaan Benar/Salah");
INSERT INTO application_description VALUES('JA', 8, "Flashは、ちょっとしたクイズを作成するためのプログラムです。様々なテンプレートから問題を選択することができます。\nを含むが、これらに限定されない。\n- 多肢選択問題\n- 複数の回答の質問\n- オープン質問\n- 公開質問（数字のみ）\n- 真/偽の質問");
INSERT INTO application_description VALUES('KO', 8, "플래시는 작은 퀴즈를 만들 수있는 프로그램입니다. 사용자가 다양한 기성품 템플릿에서 질문을 선택할 수 있는 위치.\n다음을 포함하되 이에 국한되지 않음:\n- 객관식 질문\n- 복수 답변 질문\n- 열린 질문\n- 열린 질문 VALUES(숫자만)\n- 참/거짓 질문");
INSERT INTO application_description VALUES('NL', 8, "Flash is een programma waarmee men kleine quizzen kan maken. Waar de gebruiker vragen kan kiezen uit een aantal kant-en-klare sjablonen. Met inbegrip van, maar niet beperkt tot:\nMeerkeuzevragen\n- Meervoudige antwoordvragen\n- Open vragen\n- Open vragen  VALUES(alleen getallen)\n- Waar/onwaar vragen");
INSERT INTO application_description VALUES('PT', 8, "Flash é um programa que permite criar pequenos testes. Onde o usuário pode escolher perguntas de uma variedade de modelos prontos.\nIncluindo, mas não se limitando a:\n- Perguntas de múltipla escolha\n- Perguntas múltiplas\n- Perguntas abertas\n- Perguntas abertas  VALUES(somente números)\n- Perguntas verdadeiras/falsas");
INSERT INTO application_description VALUES('RU', 8, "Flash - это программа, которая позволяет создавать небольшие викторины. Где пользователь может выбирать вопросы из множества готовых шаблонов.\n Включая, но не ограничиваясь: \n- Вопросы с множественным выбором \n- Вопросы с несколькими ответами \n- Открытые вопросы \n- Открытые вопросы  VALUES(только цифры) \n- Истинные/Ложные вопросы");
INSERT INTO application_description VALUES('TR', 8, "Flash, birinin küçük sınavlar oluşturmasına izin veren bir programdır. Kullanıcının çeşitli hazır şablonlardan soru seçebileceği yerler.\nAşağıdakiler dahil ancak bunlarla sınırlı olmamak üzere:\n- Çoktan Seçmeli Sorular\n- Çok Yanıtlı Sorular\n- Açık Sorular\n- Açık Sorular  VALUES(yalnızca sayılar)\n- Doğru/Yanlış Sorular");
INSERT INTO application_description VALUES('ZH', 8, "Flash 是一种允许人们创建小测验的程序。用户可以从各种现成的模板中选择问题。\n包括但不限于：\n- 多选题\n- 多选题\n- 开放式问题\n- 开放式问题（仅限数字）\n- 真/假问题");

INSERT INTO application_description VALUES('AR', 9, "احصل على نظرة ثاقبة في وقت اللعب في السماء والإحصائيات الأخرى. من خلال تحليل لقطات الشاشة التي التقطتها خلال رحلتك عبر عالم السماء: أطفال النور.");
INSERT INTO application_description VALUES('DE', 9, "Gewinnen Sie einen Einblick in Ihre Sky Playtime & andere Statistiken. Indem Sie die Screenshots analysieren, die Sie während Ihrer Reise durch die Welt von Sky: Children Of The Light gemacht haben.");
INSERT INTO application_description VALUES('EN', 9, "Gain an insight in your Sky playtime & other statistics. By analyzing the screenshots you took during your journey through the world of Sky: Children Of The Light.");
INSERT INTO application_description VALUES('ES', 9, "Obtenga una visión de su tiempo de juego en Sky y otras estadísticas. Analizando las capturas de pantalla que tomaste durante tu viaje por el mundo de Sky: Children Of The Light.");
INSERT INTO application_description VALUES('ET', 9, "Saate ülevaate oma Sky mänguajast ja muust statistikast. Analüüsides ekraanipilte, mille tegite oma teekonnal läbi Sky: Children Of The Lighti maailma.");
INSERT INTO application_description VALUES('FR', 9, "Obtenez un aperçu du temps de jeu de votre Sky et d'autres statistiques. En analysant les captures d'écran que vous avez prises pendant votre voyage dans le monde de Sky : Children Of The Light.");
INSERT INTO application_description VALUES('HI', 9, "अपने स्काई प्लेटाइम और अन्य आंकड़ों में अंतर्दृष्टि प्राप्त करें। स्काई: चिल्ड्रन ऑफ़ द लाइट की दुनिया में अपनी यात्रा के दौरान आपके द्वारा लिए गए स्क्रीनशॉट का विश्लेषण करके।");
INSERT INTO application_description VALUES('ID', 9, "Dapatkan wawasan tentang waktu bermain Sky Anda & statistik lainnya. Dengan menganalisis tangkapan layar yang Anda ambil selama perjalanan Anda melalui dunia Sky: Children Of The Light.");
INSERT INTO application_description VALUES('JA', 9, "Skyのプレイ時間やその他の統計情報を見ることができます。Skyの世界を旅している間に撮ったスクリーンショットを解析することでChildren Of The Lightの世界を旅して撮影したスクリーンショットを分析します。");
INSERT INTO application_description VALUES('KO', 9, "Sky 플레이 시간 및 기타 통계에 대한 통찰력을 얻으십시오. 하늘의 세계를 여행하는 동안 찍은 스크린 샷을 분석하여 : 빛의 아이들.");
INSERT INTO application_description VALUES('NL', 9, "Krijg inzicht in je Sky speeltijd & andere statistieken. Door de screenshots te analyseren die je nam tijdens je reis door de wereld van Sky: Children Of The Light.");
INSERT INTO application_description VALUES('PT', 9, "Obtenha uma visão do seu sky playtime e outras estatísticas. Analisando as capturas de tela que você tirou durante sua jornada pelo mundo de Sky: Children Of The Light.");
INSERT INTO application_description VALUES('RU', 9, "Получите представление о вашем игровом времени Sky и другой статистике. Проанализировав скриншоты, которые вы сделали во время своего путешествия по миру Sky: Children Of The Light.");
INSERT INTO application_description VALUES('TR', 9, "Sky oyun süreniz ve diğer istatistikleriniz hakkında bilgi edinin. Gökyüzü dünyasındaki yolculuğunuz sırasında aldığınız ekran görüntülerini analiz ederek: Işığın Çocukları.");
INSERT INTO application_description VALUES('ZH', 9, "深入了解您的 Sky 游戏时间和其他统计数据。通过分析您在天空世界之旅中拍摄的屏幕截图：光之子。");

INSERT INTO application_description VALUES('AR', 10, "تكما هي أداة إنتاجية بنمط الكانبان تمكّنك من إدارة مهامك ومشاريعك بكفاءة وسهولة.\nمستوحاة من منصة تريلو المشهورة، تقدم تكما واجهة أنيقة وبديهية مصممة لتعكس المظهر والشعور المألوف لتريلو. ما يميز تكما، ومع ذلك، هو وظيفتها غير المسبوقة في العمل دون اتصال، حيث تمكنك من العمل بسلاسة دون اعتماد على خوادم الطرف الثالث. مع تكما، تتمتع بمرونة في العمل على مشاريعك في أي وقت وأي مكان، دون التنازل عن خصوصية البيانات أو اتصال الإنترنت. اختبر الدفعة النهائية للإنتاجية مع تجربة تكما الكانبان غير المتصلة بسلاسة.");
INSERT INTO application_description VALUES('DE', 10, "Takma ist ein Produktivitätswerkzeug im Kanban-Stil, das Ihnen ermöglicht, Ihre Aufgaben und Projekte effektiv und problemlos zu verwalten. \nInspiriert von der renommierten Trello-Plattform bietet Takma eine elegante und intuitive Benutzeroberfläche, die entworfen wurde, um das vertraute Erscheinungsbild von Trello widerzuspiegeln. Was Takma jedoch von anderen unterscheidet, ist seine unvergleichliche Offline-Funktionalität, die es Ihnen ermöglicht, nahtlos offline zu arbeiten, ohne von Drittanbieter-Servern abhängig zu sein. Mit Takma haben Sie die Flexibilität, an Ihren Projekten jederzeit und überall zu arbeiten, ohne dabei auf Datenschutz oder Internetverbindung verzichten zu müssen. Erleben Sie den ultimativen Produktivitätsschub mit Takmas nahtloser Offline-Kanban-Erfahrung.");
INSERT INTO application_description VALUES('EN', 10, "Takma is a Kanban-style productivity tool that empowers you to effectively manage your tasks and projects with ease. \nDrawing inspiration from the renowned Trello platform, Takma offers a sleek and intuitive interface designed to mirror the familiar look and feel of Trello. What sets Takma apart, however, is its unparalleled offline functionality, allowing you to seamlessly work offline without any dependence on third-party servers. With Takma, you have the flexibility to work on your projects anytime, anywhere, without compromising on data privacy or internet connectivity. Experience the ultimate productivity boost with Takma's seamless offline Kanban experience.");
INSERT INTO application_description VALUES('ES', 10, "Takma es una herramienta de productividad en el estilo Kanban que te permite gestionar tus tareas y proyectos de manera efectiva y sencilla.\nTomando inspiración de la reconocida plataforma Trello, Takma ofrece una interfaz elegante e intuitiva diseñada para reflejar la apariencia y sensación familiar de Trello. Lo que diferencia a Takma, sin embargo, es su inigualable funcionalidad sin conexión, que te permite trabajar sin problemas sin depender de servidores de terceros. Con Takma, tienes la flexibilidad para trabajar en tus proyectos en cualquier momento y lugar, sin comprometer la privacidad de los datos ni la conectividad a Internet. Experimenta el impulso definitivo en la productividad con la experiencia sin interrupciones de Kanban sin conexión de Takma.");
INSERT INTO application_description VALUES('ET', 10, "Takma on Kanban-stiilis produktiivsusrakendus, mis võimaldab teil oma ülesandeid ja projekte tõhusalt ja lihtsalt hallata.\nSaades inspiratsiooni tuntud Trello platvormist, pakub Takma elegantset ja intuitiivset liidest, mis on kujundatud peegeldama Trello tuttavat välimust ja tunnet. Kuid see, mis eristab Takmat, on tema enneolematu võimalus töötada võrguühenduseta, võimaldades teil sujuvalt töötada ilma sõltuvuseta kolmandate osapoolte serveritest. Takmaga saate paindlikult töötada oma projektide kallal igal ajal ja igal pool, kartmata andmete privaatsuse või internetiühenduse kaotust. Kogege lõplikku produktiivsuse tõusu Takma sujuva võrguühenduseta Kanban kogemuse abil.");
INSERT INTO application_description VALUES('FR', 10, "Takma est un outil de productivité de style Kanban qui vous permet de gérer efficacement vos tâches et projets en toute simplicité.\nS'inspirant de la célèbre plateforme Trello, Takma offre une interface élégante et intuitive conçue pour refléter l'apparence et le ressenti familiers de Trello. Ce qui distingue Takma, cependant, c'est sa fonctionnalité hors ligne inégalée, vous permettant de travailler sans problème hors ligne sans aucune dépendance à l'égard de serveurs tiers. Avec Takma, vous avez la flexibilité de travailler sur vos projets à tout moment, n'importe où, sans compromettre la confidentialité des données ni la connectivité Internet. Découvrez l'ultime coup de pouce en matière de productivité avec l'expérience Kanban hors ligne fluide de Takma.");
INSERT INTO application_description VALUES('HI', 10, "Takma एक कैनबैन-शैली की उत्पादकता टूल है जो आपको आसानी से अपने कार्यों और परियोजनाओं का प्रभावी प्रबंधन करने में सशक्त बनाता है।\nप्रसिद्ध Trello प्लेटफ़ॉर्म से प्रेरित होकर, Takma एक सुंदर और सूचनात्मक इंटरफ़ेस प्रदान करता है जिसका डिज़ाइन Trello की परिचित दिखावट और महसूस को दर्शाने के लिए किया गया है। हालांकि, तकमा को अनपरिवर्तित ऑफ़लाइन कार्यक्षमता में उन्नत बनाता है, जो आपको तीसरे पक्ष सर्वरों पर किसी भी आश्रितता के बिना बिना तकलीफ़ ही ऑफ़लाइन में काम करने की अनुमति देता है। तकमा के साथ, आपको डेटा गोपनीयता या इंटरनेट कनेक्टिविटी पर कोई समझौता नहीं करने की आवश्यकता नहीं है, आप अपनी परियोजनाओं पर किसी भी समय, कहीं भी काम करने की लचीलता रखते हैं। तकमा की बिना किसी अविवादित ऑफ़लाइन कैनबैन अनुभव के साथ आप आखिरी उत्पादकता की बढ़ोतरी का अनुभव करें।");
INSERT INTO application_description VALUES('ID', 10, "Takma adalah alat produktivitas gaya Kanban yang memberdayakan Anda untuk mengelola tugas dan proyek Anda dengan efektif dan mudah.\nMengambil inspirasi dari platform terkenal Trello, Takma menawarkan antarmuka yang elegan dan intuitif yang dirancang untuk mencerminkan tampilan dan nuansa yang akrab dari Trello. Namun, yang membedakan Takma adalah fungsinya yang tak tertandingi secara offline, memungkinkan Anda bekerja secara lancar tanpa ketergantungan pada server pihak ketiga. Dengan Takma, Anda memiliki fleksibilitas untuk bekerja pada proyek Anda kapan saja, di mana saja, tanpa mengorbankan privasi data atau konektivitas internet. Rasakan dorongan produktivitas utama dengan pengalaman Kanban offline yang lancar dari Takma.");
INSERT INTO application_description VALUES('JA', 10, "Takmaは、カンバンスタイルの生産性ツールで、タスクとプロジェクトを効果的に簡単に管理する力を提供します。\n名高いTrelloプラットフォームからインスピレーションを得て、Takmaは、Trelloの見慣れた外観と雰囲気を反映させるようにデザインされた、洗練された直感的なインターフェースを提供します。しかし、Takmaの特徴的な部分は、比類のないオフライン機能です。サードパーティのサーバーに依存することなく、シームレスにオフラインで作業することができます。Takmaを使用することで、データのプライバシーやインターネット接続を犠牲にすることなく、いつでもどこでもプロジェクトに取り組む柔軟性があります。Takmaのシームレスなオフラインカンバン体験で究極の生産性向上を体験してください。");
INSERT INTO application_description VALUES('KO', 10, "Takma는 칸반 스타일의 생산성 도구로, 작업과 프로젝트를 효과적으로 쉽게 관리할 수 있도록 도와줍니다.\n유명한 Trello 플랫폼에서 영감을 얻어 Takma는 Trello의 익숙한 외관과 느낌을 반영하는 세련되고 직관적인 인터페이스를 제공합니다. 그러나 Takma를 독특하게 만드는 것은 전례없는 오프라인 기능입니다. 타사 서버에 의존하지 않고 원활하게 오프라인에서 작업할 수 있습니다. Takma를 사용하면 데이터 개인 정보 보호나 인터넷 연결을 희생하지 않고 언제 어디서든 프로젝트에 작업할 수 있는 유연성을 갖게 됩니다. Takma의 원활한 오프라인 칸반 경험으로 궁극적인 생산성 향상을 경험해보세요.");
INSERT INTO application_description VALUES('NL', 10, "Takma is een productiviteitstool in Kanban-stijl die u in staat stelt om uw taken en projecten effectief en gemakkelijk te beheren.\nGeïnspireerd door het bekende Trello-platform, biedt Takma een strakke en intuïtieve interface die is ontworpen om het vertrouwde uiterlijk en gevoel van Trello te weerspiegelen. Wat Takma echter onderscheidt, is de ongeëvenaarde offline functionaliteit, waardoor u naadloos offline kunt werken zonder enige afhankelijkheid van servers van derden. Met Takma heeft u de flexibiliteit om altijd en overal aan uw projecten te werken, zonder concessies te doen aan de privacy van gegevens of internetconnectiviteit. Ervaar de ultieme productiviteitsboost met Takma's naadloze offline Kanban-ervaring.");
INSERT INTO application_description VALUES('PT', 10, "Takma é uma ferramenta de produtividade no estilo Kanban que capacita você a gerenciar efetivamente suas tarefas e projetos com facilidade.\nInspirado na renomada plataforma Trello, o Takma oferece uma interface elegante e intuitiva projetada para espelhar o visual e a sensação familiar do Trello. O que diferencia o Takma, no entanto, é sua funcionalidade offline incomparável, permitindo que você trabalhe offline sem depender de servidores de terceiros. Com o Takma, você tem a flexibilidade de trabalhar em seus projetos a qualquer momento e em qualquer lugar, sem comprometer a privacidade dos dados ou a conectividade com a internet. Experimente o impulso máximo de produtividade com a experiência de Kanban offline perfeita do Takma.");
INSERT INTO application_description VALUES('RU', 10, "Takma - это инструмент для повышения продуктивности в стиле Канбан, который позволяет вам эффективно управлять задачами и проектами с легкостью.\nИспользуя вдохновение от известной платформы Trello, Takma предлагает элегантный и интуитивный интерфейс, разработанный для отражения знакомого внешнего вида и ощущения Trello. Однако то, что выделяет Takma, это его беспрецедентная функциональность в офлайн-режиме, позволяющая вам бесперебойно работать без подключения к сторонним серверам. С помощью Takma у вас есть гибкость для работы над проектами в любое время и в любом месте, не нарушая конфиденциальность данных или подключение к интернету. Ощутите максимальное повышение продуктивности с безупречным офлайн-опытом Kanban от Takma.");
INSERT INTO application_description VALUES('TR', 10, "Takma, görevlerinizi ve projelerinizi etkili bir şekilde yönetmenize olanak tanıyan bir Kanban tarzı üretkenlik aracıdır.\nÜnlü Trello platformundan ilham alarak, Takma, Trello'nun tanıdık görünümünü ve hissini yansıtacak şekilde tasarlanmış şık ve sezgisel bir arayüz sunar. Ancak Takma'yı diğerlerinden ayıran şey, üçüncü taraf sunuculara bağımlılık olmadan sorunsuz bir şekilde çevrimdışı çalışmanıza olanak tanıyan eşsiz çevrimdışı işlevselliğidir. Takma ile projeleriniz üzerinde istediğiniz zaman ve her yerde çalışma esnekliğine sahip olursunuz; veri gizliliğini veya internet bağlantısını riske atmadan. Takma'nın sorunsuz çevrimdışı Kanban deneyimi ile en üst düzeyde üretkenlik artışını deneyimleyin.");
INSERT INTO application_description VALUES('ZH', 10, "Takma 是一款类似于看板法的生产力工具，使您能够轻松高效地管理任务和项目。\n从知名的 Trello 平台汲取灵感，Takma 提供了一个简洁直观的界面，旨在模仿 Trello 熟悉的外观和感觉。然而，Takma 的独特之处在于其无与伦比的脱机功能，无需依赖第三方服务器即可无缝脱机工作。通过 Takma，您可以随时随地在项目上工作，而不会影响数据隐私或互联网连接。体验 Takma 无缝离线看板体验，提升终极生产力。");
```
</details>

### applicationsVersions.properties
To add a new application, add two new lines to the `applicationsVersions.properties` file.
> The id for this application should be the same one that is used inside the `applications.sqlite` database:
```
appVersion<id>=<version>
appLatestUpdate<id>=<latest update in unix seconds>
```

The contents of this file may look something like this:
```
appVersion0=0.3.1
appLatestUpdate0=1684925160
appVersion1=1.0.0
appLatestUpdate1=1533340800
appVersion2=1.0.0
appLatestUpdate2=1533340800
appVersion3=1.0.0
appLatestUpdate3=1534291200
appVersion4=0.10.0
appLatestUpdate4=1574643600
appVersion5=1.0.0
appLatestUpdate5=1616029200
appVersion6=1.0.0
appLatestUpdate6=1587168000
appVersion7=1.4.2
appLatestUpdate7=1691575413
appVersion8=1.9.0
appLatestUpdate8=1653004800
appVersion9=1.1.9
appLatestUpdate9=1657238400
appVersion10=1.4.0
appLatestUpdate10=1699901910
```

### Jam54LauncherData.java
Increment the size of the `installedApplicationVersions` and `installedApplicationLatestUpdates` arrays by one.

### Hosting & hashing the application files

> Just as a small tl:dr on how the files are hosted. Basically the files are hosted as a website, the root folder of the website contains several subfolders.  
> These subfolders have the names 0 1 2 etc., which corresponds to the application id's defined inside the `applications.sqlite` database  
> 
> Each subfolder then contains all the binaries for that application  
> \+ a file called `EntryPoint.txt`. This file contains one line and points to the executable of the app.  
> \+ a file called `Hashes.txt`. This file contains all the hashes for the binaries of that application.  
> \+ a file called `Split.txt`, which may or may not be empty. It contains the path to files that were bigger than a certain amount in megabytes and that have been split into smaller chunks.
> > We split files that are larger than 99MBs into smaller chunks in order to stay below the 100MB size limit that GitHub imposes. (We host the binaries of the applications using GitHub pages)
>
> This makes it so that when we want to download a specific file of a given application. We can do that by going visiting the following url: `base url` + `subfolder (a number representing the applicationId)` + `path to file`

#### Hashing & splitting the files
- Create a "root" directory
- Create a subfolder for each of the applications; the name of a subfolder should be the *id* of the application whose files will be in the subfolder
- Place the binaries of the application in the subfolder
  - Inside the subfolder of each application, create a file called `EntryPoint.txt`. This file should only contain 1 line, the path to the main executable of the application.  
    The path should start from within the subfolder, therefore it doesn't contain the name of the subfolder itself. Here are some examples:
    ```
    <folder within subfolder>\<more folders>\<name executable>
    ```
    ```
    Stelexo.exe
    ```
    ```
    https://jam53.github.io/jam54/
    ```
    ```
    mailto:jam54.help@outlook.com
    ```
- Repeat this for all of the applications

---

- Inside the `Main.java` file, place the following code at the beginning of the main method:
    - ```java
      ArrayList<Path> paths = new ArrayList<>();

      paths.add(Path.of("pathToRootFolder\\0"));
      paths.add(Path.of("pathToRootFolder\\1"));
      // etc for all of the applications in the root folder

      FileSplitterCombiner fileSplitterCombiner = new FileSplitterCombiner();
      paths.forEach(path -> fileSplitterCombiner.splitFilesLargerThan(99, path));

      Hashes hashes = new Hashes();
      hashes.calculateHashesTXTFiles(paths);
      ```
- Run the application
  - You may get an error along the lines of `Caused by: java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because the return value of "java.util.Properties.getProperty(String)" is null`. This is fine. We just need to execute the code snippet we pasted to hash the files of the newly added app. The code that comes after it throws an error in this case because the `applicationsVersions.properties` gets downloaded from where our files are hosted. But that `applicationsVersions.properties` file is obviously still missing the entry for the newly added app. Hence why we get an error
- Remove the lines you added to the `Main.java` file in the previous step
- Each of the subfolders should now contain both a `Hashes.txt` file and a `Split.txt` file
- In root directory containing all of the subfolders, run the following command (adjust the name of the subfolders in the for loop, so that it contains all of the subfolders)
  - `for folder in 0 1 2 3 4 5 6 7 8 9; do cd "$folder" && grep -F -f Split.txt Hashes.txt | grep .part -v | tee linesToRemove.txt && grep -F -f linesToRemove.txt Hashes.txt -v | tee newHashes.txt && rm linesToRemove.txt && mv newHashes.txt Hashes.txt && cd ..; done`
    > This command will remove the lines in `Hashes.txt` that point to the original unsplitted files that were larger than the specified treshold to the `splitFilesLargerThan()` function

--- 

> Here we assume that GitHub pages is enabled, so that we can host our files

- Go to the [following repository](https://github.com/jamhorn/Jam54Launcher)
- Push all of the files in the "root" folder, to the *files* branch (make sure to use the correct Git account, by signing in and out of GitHub desktop)
  > Do note that files that were too large for GitHub (over 100MB) have been split. But the original unsplit file is also still present in the subfolder of such an application. Therefore when uploading such an application's binaries, just omit the original, unsplitted file.
- Create an empty file called `.nojekyll` in the root of the repository. Otherwise files/folders starting with `_` will be seen as jekyll files by GitHub pages
- Also place a copy of the "root" folder in: `OneDrive\Documenten\Scripts\Builds\Jam54Launcher\AppBuilds`

---

Finally rebuild and republish the launcher's binaries following the steps described in [Updating the Jam54Launcher.](./UpdatingTheJam54Launcher.md)

<br>

## Updating an application

### applications.sqlite
You can update an existing application's details by entering the following command
```sql
sqlite3 applications.sqlite

UPDATE applications SET latestUpdate=1609549200 WHERE id=0;
```

> After changing the contents of `applications.sqlite`, the `Jam54_Launcher.jar` and `Jam54LauncherSetup.msi` files will need to be rebuild and reuploaded. 
> Follow the steps described in [Updating the Jam54Launcher](./UpdatingTheJam54Launcher.md) to rebuild and upload the launcher's binaries.
> > However, this is only the case if a field other than `latestUpdate` was changed. It would be cumbersome to have to release an entire update for the launcher, just for updating an application (therefore changing the `latestUpdate` field of said application). That's why we also include the value of `latestUpdate` in the `applicationsVersions.properties` file. This file can be changed and uploaded independently from the launcher. The launcher then downloads this file on launch and will use the `latestUpdate` value in there instead of the one in the `applications.sqlite` database. In the application we take the largest `latestUpdate` value from `applications.sqlite` and `applicationsVersions.properties`. This way we always have the correct release date:
> > - When online with the launcher including an up to date version of `applicationsVersions.properties` 
> >   - max(`applicationsVersions.properties`, `applications.sqlite`) = either one
> > - When online with the launcher including an outdated version of `applicationsVersions.properties` 
> >   - max(`applicationsVersions.properties`, `applications.sqlite`) = `applicationsVersions.properties`
> > - When offline with the launcher including an up to date version of `applicationsVersions.properties` 
> >   - max(`applicationsVersions.properties`, `applications.sqlite`) = `applications.sqlite` 
> > - When offline with the launcher including an outdated version of `applicationsVersions.properties` 
> >   - max(`applicationsVersions.properties`, `applications.sqlite`) = old value of `applicationsVersions.properties` from the last time it was downloaded or `applications.sqlite` in case `applicationsVersions.properties` was never downloaded.
> > > When we are offline we can't download the `applicationsVersions.properties` file, meaning it will return `0` for a given applications `latestUpdate`. This might lead to the assumption that in the last situation, we would use an outdated `latestUpdate` from the `applications.sqlite` database.  
> > > However, we also save the `latestUpdate` value to disk everytime we download the `applicationsVersions.properties` file. So if we did download the `applicationsVersions.properties` previously when we had internet and it had an the up to date `latestUpdate` value. We will still use that when offline. You might argue that when we are never online, we will have a wrong `latestUpdate` value. Because we would have never downloaded the `applicationsVersions.properties` file. But in that case we would also never receive an update of the jam54Launcher which would include an updated `applications.sqlite` database.

### applicationsVersions.properties
Open the `applicationsVersions.properties` file, and update the value behind the = of the application in question.

> After changing the contents of `applicationsVersions.properties`, this file will need to be reuploaded. However, rebuilding and reuploading the launcher's binaries is not required.  
> Follow the steps described in [Updating the Jam54Launcher](./UpdatingTheJam54Launcher.md)

### Hosting & hashing the application files

#### Hashing & splitting the files
- Navigate to the "root" folder containing all of the apps: `OneDrive\Documenten\Scripts\Builds\Jam54Launcher\AppBuilds`
- Remove all of the files apart from `EntryPoint.txt` and `LICENSE` in the subfolder of the app which you wish to update, and place the new binaries in the subfolder. 
    - > Make sure to update the `EntryPoint.txt` file if necessary. 

---

- Inside the `Main.java` file, place the following code at the beginning of the main method:
    - ```java
      ArrayList<Path> paths = new ArrayList<>();
      paths.add(Path.of("pathToRootFolder\\0")); //Name of subfolder that corresponds to the app that we want to update

      FileSplitterCombiner fileSplitterCombiner = new FileSplitterCombiner();
      paths.forEach(path -> fileSplitterCombiner.splitFilesLargerThan(99, path));

      Hashes hashes = new Hashes();
      hashes.calculateHashesTXTFiles(paths);
      ```
- Run the application
- Remove the lines you added to the `Main.java` file in the previous step
- Each of the subfolders should now contain both a `Hashes.txt` file and a `Split.txt` file
- In root directory containing all of the subfolders, run the following command (adjust the name of the subfolders in the for loop, so that it contains all of the subfolders)
  - `for folder in 0 1 2 3 5 6 8; do cd "$folder" && grep -F -f Split.txt Hashes.txt | grep .part -v | tee linesToRemove.txt && grep -F -f linesToRemove.txt Hashes.txt -v | tee newHashes.txt && rm linesToRemove.txt && mv newHashes.txt Hashes.txt && cd ..; done`
    > This command will remove the lines in `Hashes.txt` that point to the original unsplitted files that were larger than the specified treshold to the `splitFilesLargerThan()` function

--- 

> Here we assume that GitHub pages is enabled, so that we can host our files

- Go to the [following repository](https://github.com/jamhorn/Jam54Launcher)
- Push all of the files in the "root" folder, to the *files* branch (make sure to use the correct Git account, by signing in and out of GitHub desktop)
  > - Since it's a git commit, only the updated files will actually be pushed, rather than all the files
  > - Do note that files that were too large for GitHub (over 100MB) have been split. But the original unsplit file is also still present in the subfolder of such an application. Therefore when uploading such an application's binaries, just omit the original, unsplitted files
