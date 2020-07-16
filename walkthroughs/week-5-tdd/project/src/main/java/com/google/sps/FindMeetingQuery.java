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

package com.google.sps;

import java.lang.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> relevantTimes = removeIrrelevant(events, request);
    ArrayList<TimeRange> relevantTimesWithOptional = removeIrrelevantOptional(events, request);
    Collection<TimeRange> rtn = getTime(relevantTimesWithOptional, request);
    if (request.getAttendees().isEmpty() || !rtn.isEmpty()) {
        return rtn;
    } else {
        return getTime(relevantTimes, request);
    }
    /**
    4 cases 
    1. no mandatory & no optional --> return WHOLE_DAY
    2. no mandatory & optional --> return optimize
    3. mandatory & no optional --> return normal
    4. mandatory & optional --> return optimize
    */
  }

  /** Takes all the @param EVENTS and @param REQUESTS and remove events that
  *   don't have attendees in the request meeting. @return the desired collection.
  */
  public ArrayList<TimeRange> removeIrrelevant(Collection<Event> events, MeetingRequest request) {
    Collection<String> requestAttendees = request.getAttendees();
    ArrayList<TimeRange> relevantTimes = new ArrayList<TimeRange>();
    for (Event e : events) {
        for (String s : e.getAttendees()) {
            if (requestAttendees.contains(s)) {
                relevantTimes.add(e.getWhen());
            }
        }
    }
    return relevantTimes;
  }

  /** Takes all the @param EVENTS and @param REQUESTS and remove events that
  *   don't have attendees in the request meeting consdiering optional
  *   attendees. @return the desired collection.
  */
  public ArrayList<TimeRange> removeIrrelevantOptional(Collection<Event> events, MeetingRequest request) {
    Collection<String> requestAttendees = request.getAttendees();
    ArrayList<String> allAttendees = new ArrayList<String>();
    for (String s : requestAttendees) {
        allAttendees.add(s);
    }
    for (String s : request.getOptionalAttendees()) {
        allAttendees.add(s);
    }
    ArrayList<TimeRange> relevantTimes = new ArrayList<TimeRange>();
    for (Event e : events) {
        for (String s : e.getAttendees()) {
            if (allAttendees.contains(s)) {
                relevantTimes.add(e.getWhen());
            }
        }
    }
    return relevantTimes;
  }

  /** Helper to @return the available times given @param RELEVANTTIMES and @param REQUEST. */
  public Collection<TimeRange> getTime(ArrayList<TimeRange> relevantTimes, MeetingRequest request) {
    if (relevantTimes.isEmpty()) {
        if (request.getDuration() > TimeRange.END_OF_DAY - TimeRange.START_OF_DAY) {
            return Arrays.asList();
        }
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    Collections.sort(relevantTimes, TimeRange.ORDER_BY_START);
    int start = TimeRange.START_OF_DAY;
    int end = TimeRange.END_OF_DAY;
    boolean overlapped = false;
    ArrayList<TimeRange> rtn = new ArrayList<TimeRange>();
    for (int i = 0; i < relevantTimes.size(); i++) {
        TimeRange curr = relevantTimes.get(i);
        end = curr.start();
        if (end - start >= request.getDuration() && !overlapped) {
            rtn.add(TimeRange.fromStartEnd(start, end, false));
        }
        overlapped = false;
        if (i < relevantTimes.size() - 1) {
            TimeRange next = relevantTimes.get(i + 1);
            if (!curr.overlaps(next)) {
                start = Math.max(start, curr.end());
            } else if (!curr.contains(next)) {
                start = Math.max(start, next.end());
            } else {
                start = Math.max(start, curr.end());
            }
        }
    }
    Collections.sort(relevantTimes, TimeRange.ORDER_BY_END);
    start = relevantTimes.get(relevantTimes.size() - 1).end();
    end = TimeRange.END_OF_DAY;
    if (end - start >= request.getDuration()) {
        rtn.add(TimeRange.fromStartEnd(start, end, true));
    }
    return rtn;
  }


  public void getCombination(ArrayList<String> optionalAttendees, int n, int r) {
      optionalAttendeesPermutation.clear();
      String[] data = new String[r];
      resursiveCombination(optionalAttendees, n, r, 0, data, 0);
  }

  public void resursiveCombination(ArrayList<String> optionalAttendees, int n, int r, 
            int index, String[] data, int i) {
        if (index == r) {
            optionalAttendeesPermutation.add(data.clone());
            return;
        }
        if (i >= n) {
            return;
        }
        data[index] = optionalAttendees.get(i);
        resursiveCombination(optionalAttendees, n, r, index + 1,
        data, i + 1);
        resursiveCombination(optionalAttendees, n, r, index, data, i + 1);
    }

  private ArrayList<String[]> optionalAttendeesPermutation = new ArrayList<String[]>();
}
