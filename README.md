# SpigotVisyLogger
The plugin will help you register Xray and other useful cheats
---

### **🇷🇺 Русская версия**  
**Основные возможности**:  
1. **Логирование добычи**  
   - Автоматически отслеживает добычу:  
     - Алмазов  
     - Древних обломков  
     - Незеритовых скрапов  
   - Настраивается через `config.yml`  

2. **Защита от читеров**  
   - Фиксирует подозрительную активность (например, 64 алмаза за 5 минут)  
   - Отправляет приватные уведомления операторам  

3. **Статистика игроков**  
   - Команда `/logger <ник>` - просмотр статистики  
   - Показывает:  
     - Количество добытых ресурсов  
     - Временные промежутки  

4. **Гибкая настройка**  
   - Можно добавить любые блоки из Minecraft  
   - Редактирование сообщений  

---

### **🇵🇱 Wersja polska**  
**Główne funkcje**:  
1. **Rejestrowanie wydobycia**  
   - Śledzi automatycznie:  
     - Diamenty  
     - Starożytne szczątki  
     - Złom netherytu  
   - Konfiguracja w `config.yml`  

2. **Ochrona przed oszustami**  
   - Wykrywa podejrzane działania (np. 64 diamenty w 5 minut)  
   - Wysyła prywatne powiadomienia do operatorów  

3. **Statystyki graczy**  
   - Komenda `/logger <gracz>` - przegląd statystyk  
   - Pokazuje:  
     - Liczbę zebranych zasobów  
     - Przedziały czasowe  
---

### **🇬🇧 English Version**  
**Key Features**:  
1. **Mining Logger**  
   - Tracks automatically:  
     - Diamonds  
     - Ancient Debris  
     - Netherite Scrap  
   - Configurable via `config.yml`  

2. **Anti-Cheat Protection**  
   - Detects suspicious activity (e.g. 64 diamonds in 5 mins)  
   - Sends private alerts to operators  

3. **Player Statistics**  
   - Command `/logger <player>` - view stats  
   - Displays:  
     - Resources mined  
     - Time frames  
---

### **Примеры конфигурации / Przykłady konfiguracji / Configuration Examples**  
```yaml
# Русский
messages:
  alert: "&c[ALERT] &fИгрок &e{nick} &fдобыл &a{count} &fпредметов &b{item} &fза &e{time} сек!"

# Polski
messages:
  alert: "&c[ALERT] &fGracz &e{nick} &fwydobył &a{count} &fprzedmiotów &b{item} &fw &e{time} sekund!"

# English
messages:
  alert: "&c[ALERT] &fPlayer &e{nick} &fmined &a{count} &fof &b{item} &fin &e{time} sec!"
```

**Поддержка**:  
- По вопросам обращайтесь с примером ошибки и версией сервера

