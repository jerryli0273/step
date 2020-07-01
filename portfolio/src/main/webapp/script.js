// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Bazinga!', 'How you doin?', 'You see, Watson, but you do not observe.'
      , 'What do you do Barney? Pssssh, pleeease.'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Fetches the information on the /data page and presents it in the fetch
 * containter.
 */
async function getFetch() {
  const response = await fetch('/data');
  const fetchResponse = await response.json();
  const output = document.getElementById('fetch-container');
  output.innerHTML = '';
  var i;
  for (i = 0; i < fetchResponse.length; i++) {
    output.appendChild(
      createListElement(fetchResponse[i]));
  }
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}
