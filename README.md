<p><h1>Учебный проэкт предстовляющий из себя сервис для загрузки и просмотра коротких видео.Это андроид приложение которое в качестве серверной части использует firebase.</h1></p>
<p><h3>Технологии и библиотеки используемые проэкте:</h3></p>
<p><h5>Firebase.</h5></p>
<p><h5>RecyclerView.</h5></p>
<p><h5>ViewPager2.</h5></p>
<p><h5>ExoPlayer2.</h5></p>
<p><h5>FFmpeg.</h5></p>

<img src="https://github.com/kiselyv77/RsesTok/blob/master/screenshots/Home.jpg" width="30%" height="30%" align="left" />
<big><h2>Главный экран</h2></big>
<br>
<p>На главном экране собранны видеоролики от всех пользователей.Для прокрутки видеороликов используется библиотека ViewPager2 потому как такой формат рокрутки очен удобен для коротких видеорольков.Для воспроизведения видеороликов используется ExoPlayer2 так как эта библиотека имеет очень подробную документацию и предостовляет много возможностей для показа видеороликов.</p>
<hr align="center" color="#fff" size="1" width="860px" />

<img src="https://github.com/kiselyv77/RsesTok/blob/master/screenshots/Search.jpg" width="30%" height="30%" align="left" />
<big><h2>Поиск</h2></big>
<br>
<p>На экране поска собранны все зарегистрированные пользователи.Для реализации списка использован RecyclerView. В качестве поля поиска используется SearchView. Поиск производится по имени и фамилии.</p>
<hr align="center" color="#fff" size="1" width="860px" />

<img src="https://github.com/kiselyv77/RsesTok/blob/master/screenshots/Add.jpg" width="30%" height="30%" align="left" />
<big><h2>Добавить</h2></big>
<br>
<p>На этом экране добавить видео с устройства или снять его не выходя из приложения. Для этого  используется ActivityResultLauncher. После выбора можно указать название и описание к видеоролику.Превью берется из видео с помщью MediaMetadataRetriever.Перед отправкой на сервер видео сжимается при помощи библиотеки FFmpeg что позволяет сэкономить мето в облачном хранилище, а так же ускоряет загруку видеороликов. FFmpeg предоставляет конвертировать и сжимать видео с различными настройками</p>
<hr align="center" color="#fff" size="1" width="860px" />

<img src="https://github.com/kiselyv77/RsesTok/blob/master/screenshots/Messenger.jpg" width="30%" height="30%" align="left" />
<big><h2>Сообщения</h2></big>
<br>
<p>На этом экране собранны все переписки с другими пользователями.Для реализации списка использован RecyclerView. В качестве поля поиска используется SearchView. Поиск производится по имени и фамилии.При нажатии открывается чат с пользователем.</p>
<hr align="center" color="#fff" size="1" width="860px" />

<img src="https://github.com/kiselyv77/RsesTok/blob/master/screenshots/Profile.jpg" width="30%" height="30%" align="left" />
<big></h2>Профиль<h2></big>
<br>
<p>Здесь предоставленна информация о вашем профиле. Показан список всех видеороликов. Здесь можно поменять фото, изменить данные и выйти из профиля</p>
<hr align="center" color="#fff" size="1" width="860px" />






    
    
