/**
 * Driver:     Atomic Driver
 * Author:     Mirco Caramori
 * Repository: https://github.com/mircolino/ecowitt
 * Import URL: https://raw.githubusercontent.com/mircolino/ecowitt/master/ecowitt_gateway.groovy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
*/

// Metadata -------------------------------------------------------------------------------------------------------------------

metadata {
  definition(name: "Atomic Driver", namespace: "mircolino", author: "Mirco Caramori") {
    capability "Sensor";

    attribute "child", "string";
    attribute "counter", "number";

    command "incrementCounter";
  }

  preferences {
    if (!parent) {
      input(name: "logLevel", type: "enum", title: "<font style='font-size:12px; color:#1a77c9'>Log Verbosity</font>", description: "<font style='font-size:12px; font-style: italic'>Default: 'Debug' for 30 min and 'Info' thereafter</font>", options: [0:"Error", 1:"Warning", 2:"Info", 3:"Debug"], multiple: false, defaultValue: 3, required: true);
    }
  }
}

// Logging --------------------------------------------------------------------------------------------------------------------

int getLogLevel() {
  //
  // Get the log level as an Integer:
  //
  //   0) log only Errors
  //   1) log Errors and Warnings
  //   2) log Errors, Warnings and Info
  //   3) log Errors, Warnings, Info and Debug (everything)
  //
  // If the level is not yet set in the driver preferences, return a default of 2 (Info)
  // Declared public because it's being used by the child-devices as well
  //
  if (settings.logLevel != null) return (settings.logLevel as Integer);
  return (2);
}

// ------------------------------------------------------------

private void logError(String str) {
  log.error(str);
}

// ------------------------------------------------------------

private void logWarning(String str) {
  int level = parent? parent.getLogLevel(): getLogLevel();
  if (level > 0) log.warn(str);
}

// ------------------------------------------------------------

private void logInfo(String str) {
  int level = parent? parent.getLogLevel(): getLogLevel();
  if (level > 1) log.info(str); 
}

// ------------------------------------------------------------

private void logDebug(String str) {
  int level = parent? parent.getLogLevel(): getLogLevel();
  if (level > 2) log.debug(str); 
}

// Commands -------------------------------------------------------------------------------------------------------------------

void incrementCounter() {

  if (!parent) {
    getChildDevice(state.child).incrementCounter();
  }
  else {
    int val;

    if (state.counter == null) val = 0;
    else val = state.counter + 1;

    state.counter = val;
    sendEvent(name: "counter", value: val);
  }
}

// Driver lifecycle -----------------------------------------------------------------------------------------------------------

void installed() {
  //
  // Called once when the driver is created
  //
  try {
    logDebug("installed()");

    // If we are the parent, add a child
    if (!parent) {
      String childDni = "FEDCBA987654";

      logDebug("addChild(${childDni})");

      addChildDevice("Atomic Driver", childDni, [name: "Adomic Child"]);
      state.child = childDni;
      sendEvent(name: "child", value: childDni);

      // Initialize child counter to 0
      incrementCounter();
    }
  }
  catch (Exception e) {
    logError("Exception in installed(): ${e}");
  }
}

// ------------------------------------------------------------

void updated() {
  //
  // Called everytime the user saves the driver preferences
  //
  try {
    logDebug("updated()");
  }
  catch (Exception e) {
    logError("Exception in updated(): ${e}");
  }
}

// ------------------------------------------------------------

void uninstalled() {
  //
  // Called once when the driver is deleted
  //
  try {
    logDebug("uninstalled()");

    // If we are the parent, delete all children
    if (!parent) {
      getChildDevices().each {
        logDebug("deleteChild(${it.deviceNetworkId})");

        deleteChildDevice(it.deviceNetworkId)
      }
    }
  }
  catch (Exception e) {
    logError("Exception in uninstalled(): ${e}");
  }
}

// ------------------------------------------------------------

void parse(String msg) {
  //
  // Called everytime the driver received data
  //
  try {
    logDebug("parse()");
  }
  catch (Exception e) {
    logError("Exception in parse(): ${e}");
  }
}

// EOF ------------------------------------------------------------------------------------------------------------------------
