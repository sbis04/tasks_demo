# Tasks (Android)

This is a basic TODO app to demonstrate the use of `codemagic.yaml` for building native Android app on [Codemagic](https://codemagic.io/start/).

## Screenshots

<p align="center">
  <img width="250" src="https://github.com/sbis04/tasks/raw/master/screenshots/tasks_1.png" alt="Tasks"/>&nbsp;&nbsp;&nbsp;
  <img width="250" src="https://github.com/sbis04/tasks/raw/master/screenshots/tasks_2.png" alt="Tasks"/>&nbsp;&nbsp;&nbsp;
  <img width="250" src="https://github.com/sbis04/tasks/raw/master/screenshots/tasks_3.png" alt="Tasks"/>
</p>

## Codemagic YAML Template

```yaml
# You can get more information regarding the YAML file here:
# https://docs.codemagic.io/building/yaml

# Workflow setup for building Native Android project
workflows:
  # The following workflow is for generating a debug build
  debug-workflow: # workflow ID
    name: Native Android # workflow name
    max_build_duration: 60 # max build duration in minutes
    scripts:
      - |
        # launching android emulator
        emulator -avd emulator > /dev/null 2>&1 &
      - |
        # set up debug keystore
        rm -f ~/.android/debug.keystore
        keytool -genkeypair \
          -alias androiddebugkey \
          -keypass android \
          -keystore ~/.android/debug.keystore \
          -storepass android \
          -dname 'CN=Android Debug,O=Android,C=US' \
          -keyalg 'RSA' \
          -keysize 2048 \
          -validity 10000
      - |
        # run tests
        ./gradlew test
        ./gradlew connectedAndroidTest
      - |
        # build debug apk
        ./gradlew assembleDebug
    artifacts:
      - app/build/**/outputs/**/*.apk
    publishing:
      email:
        recipients:
          - name@example.com # enter your email id here

  # The following workflow is for generating a release build
  release-workflow: # workflow ID
    name: Native Android # workflow name
    max_build_duration: 60 # max build duration in minutes
    environment:
      vars:
        CM_KEYSTORE: Encrypted(...) # enter the encrypted version of your keystore file
        CM_KEYSTORE_PASSWORD: Encrypted(...) # enter the encrypted version of your keystore password
        CM_KEY_ALIAS_PASSWORD: Encrypted(...) # enter the encrypted version of your key alias password
        CM_KEY_ALIAS_USERNAME: app # enter your alias name
    scripts:
      - |
        # launching android emulator
        emulator -avd emulator > /dev/null 2>&1 &
      - |
        # set up release keystore
        echo $CM_KEYSTORE | base64 --decode > /tmp/keystore.keystore
        cat >> "$FCI_BUILD_DIR/key.properties" <<EOF
        storePassword=$CM_KEYSTORE_PASSWORD
        keyPassword=$CM_KEY_ALIAS_PASSWORD
        keyAlias=$CM_KEY_ALIAS_USERNAME
        storeFile=/tmp/keystore.keystore
        EOF
      - |
        # run tests
        ./gradlew test
        ./gradlew connectedAndroidTest
      - |
        # build release apk
        ./gradlew assembleRelease
    artifacts:
      - app/build/**/outputs/**/*.apk
    publishing:
      email:
        recipients:
          - name@example.com # enter your email id here
```

## License

Copyright (c) 2020 Souvik Biswas

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
