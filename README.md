# SmartNFCBracelet-Based-Shopping
A UbiComp based approach to study a feasible alternative to conventional self-checkout.

# DEPLOYMENT GUIDE 

## 1. Target Audience 

The target audience for this deployment plan is Android smartphone users who have some basic technical knowledge with prior knowledge of building and running Android applications. 

2. Prerequisites 

Before proceeding with the deployment, make sure the following prerequisites are met: 

- The user's smartphone is running Android OS (version 5.0+), SDK (version 29+) and is equipped with an NFC sensor that supports tap and pay functionality. 

- The user has a PC running Windows OS (version 8+). 

- The user has at least one NFC/RFID chip that supports multiple read/write with a storage capacity of at least 200 kilobytes. 

- The user has a reliable internet connection and both the smartphone, and the PC are connected to the same network. 

- The user has installed Git, Java Development Kit (JDK version 8+), and Android Studio (version 3.3+) 

3. Step-by-Step Instructions 

3.1 Cloning the Repository 

- Open the command prompt or terminal on your desktop. 

- Type the following command to clone the repository: git clone <repository URL> 

- Replace <repository URL> with the actual URL of the repository. 

- Enter your Git credentials, if prompted. 

3.2 Build and Install the Android application. 

- Open the command prompt or terminal on your desktop and enter the command ipconFigure. From the list of IP addresses, copy the IPv4 address. 

- Open Android Studio and select "Open an existing Android Studio project" from the main menu. 

- Navigate to the location where the project is stored on your computer, select the project's folder, and click "OK." 

- Android Studio will now load the project. Wait for the Gradle build process to finish. 

- Once the Gradle build finishes, open the class file named ShopActivity.java located in app>src>main>java>com.example.smartnfccheckout. 

- In the ShopActivity.java file, navigate to line #29 and replace the value of the IP_CONFIGURE constant (after http://) with the IPv4 address copied in the first step. 

- Press the Ctrl+F9 keys to build the project. Alternatively, you can also click on the green hammer build icon. 

- Once the project is built, connect your smartphone to your computer using a USB cable. 

- Enable developer mode on your smartphone by going to Settings > System > About phone > Software information > Build number. Tap on Build number 7 times to enable developer mode. 

- Enable USB debugging by going to Settings > System > Developer options > USB debugging. 

- In Android Studio, click on Run > Run 'app' or press the Shift + F10 keys. 

- Select your smartphone from the list of available devices. If you don't see your phone listed, make sure that you have installed the appropriate drivers for your phone. 

- Click on OK to install the app on your smartphone. Wait for the app to finish installing, the app should launch itself. You should now be able to test the app on your phone and use it just as you would any other app. 

3.3 Testing the application. 

Once the application is installed, on the home screen, you would get two buttons; one for Shop mode and another for RW(Read/Write) mode. On clicking either, you would be prompted to each functionality provided by the app such that the former mode is used by a user administrator while the latter mode(s) are used by the shoppers in general. As a pre-requisite, on your PC you have to enable Internet Information Services by following the steps viz. 

- Open the Control Panel and click on "Programs" or "Programs and Features", depending on your Control Panel view. 

- In the left-hand pane, click on "Turn Windows features on or off". 

- In the "Windows Features" dialog box, scroll down and find "Internet Information Services". Check the box and press OK. You have to wait for a few minutes for the changes to get applied. 

- Once the configuration is complete navigate to the cloned repo’s root directory and copy the test folder inside the WoZ files folder. 

- On your PC, navigate to the C:\inetpub\wwwroot directory and paste the test folder. 

3.3.1 WRITE mode. 

- Enable NFC on the smartphone or else you would be asked to turn it on by being automatically directed to the settings menu on clicking the RW Mode button. 

- On the app’s home screen, press the RW Mode button followed by the Write button. Enter the relevant product name and product price in each of the text fields named “Product Name” and “Product Price $” respectively. 

- Take the NFC tag near the smartphone’s NFC sensor and press the Write button on the screen. 

- If the data is successfully written onto the tag, you would get a toast with a success message. Alternatively, if an error is encountered, you would get an error message and you have to press the Write button again. 

3.3.2 READ mode. 

- Install any NFC writer app from Google Play Store (NFC Tools) and using it, write the product list JSON data (Appendix 6.2) onto the NFC tag. (The requirement for a third-party app to write the data is because currently, the app’s Write Mode doesn’t support random text entry.) 

- Enable NFC on the smartphone or else you would be asked to turn it on by being automatically directed to the settings menu on clicking the RW Mode button. 

- On the app’s home screen, press the RW Mode button followed by the Read button.  

- Tap the smartphone’s NFC sensor onto the tag and you would get a “New Tag detected” prompt. Click on the OK button and you can see the list of items that are part of the cart as a list that can be checked out. 

3.3.3 SHOP mode/WoZ mode. 

This mode as part of the evaluation was carried out with the help of an external NFC reader which was part of the hardware implementation to perform the WoZ technique. However, even without the NFC reader, the process can be replicated by following the below-mentioned steps. 

- On your PC, navigate to the C:\inetpub\wwwroot\test directory and open the Ping.txt file in Notepad. Initially, the file is completely empty. You have to type “1” on the first line of the file without any spaces and save it. 

- Concurrently, on the app’s home screen, press the Shop Mode button. The moment you save the Ping.txt file with the content as “1”, the app screen should display the product name and price on the screen and the Proceed button is enabled. 

- Now, in the same Ping,txt file, empty the contents and save it. On the app, click on the Proceed button and the contents of the product should disappear with the Proceed button disabled.  

- You can repeat the first three steps and each time, a different product’s name and price would be displayed. 
