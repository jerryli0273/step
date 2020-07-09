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

/** Imports charts API.  */
google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

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
async function getComment() {
  const numComments = document.getElementById("numComments").value;
  const langComments = document.getElementById("langComments").value;
  const response = await fetch('/data?numComments=' + numComments 
  + '&langComments=' + langComments);
  const fetchResponse = await response.json();
  const output = document.getElementById('comment-container');
  output.innerHTML = '';
  var i;
  for (i = 0; i < fetchResponse.length; i++) {
    output.appendChild(createListElement(fetchResponse[i]));
  }
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');

  const commentElement = document.createElement('span');
  commentElement.innerText = text.body + ' ';

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
      deleteComment(text);
      liElement.remove();
  });

  liElement.appendChild(commentElement);
  liElement.appendChild(deleteButtonElement);
  return liElement;
}

/** Tells server to delete a comment. */
function deleteComment(text) {
    const params = new URLSearchParams;
    params.append('commentId', text.id);
    fetch('/delete-comment', {method: 'POST', body: params});
}

/** Creates a chart and adds it to the page. */
function drawChart() {
  fetch('/question-data').then(response => response.json())
  .then((questionVotes) => {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Question');
    data.addColumn('number', 'Votes');
    Object.keys(questionVotes).forEach((question) => {
      data.addRow([question, questionVotes[question]]);
    });

    const options = {
      'title': 'Asked Questions',
      'width':600,
      'height':500
    };

    const chart = new google.visualization.ColumnChart(
        document.getElementById('chart-container'));
    chart.draw(data, options);
  });
}
