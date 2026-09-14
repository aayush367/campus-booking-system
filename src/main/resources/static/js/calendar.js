/* Weekly time-grid calendar renderer — matches the Figma prototype.
   window.CALENDAR_EVENTS = [{id,room,date,startTime,endTime,status,requestedBy,...}]
   Renders into #calendarGrid; uses #calendarRangeLabel / #calendarPrev / #calendarNext. */
(function () {
  "use strict";

  var DAY_START = 8;   // 8:00 AM
  var DAY_END = 21;    // 9:00 PM
  var SLOT_PX = 56;    // must match .cal-slot / .cal-hour height in calendar.css

  function parseDate(str) {
    var p = (str || "").split("-");
    return p.length === 3 ? new Date(+p[0], +p[1] - 1, +p[2]) : null;
  }
  function toMinutes(t) {
    var p = (t || "").split(":");
    return p.length >= 2 ? (+p[0]) * 60 + (+p[1]) : null;
  }
  function startOfWeek(d) {
    var c = new Date(d.getFullYear(), d.getMonth(), d.getDate());
    var day = (c.getDay() + 6) % 7; // Monday = 0
    c.setDate(c.getDate() - day);
    return c;
  }
  function sameDay(a, b) {
    return a.getFullYear() === b.getFullYear()
        && a.getMonth() === b.getMonth()
        && a.getDate() === b.getDate();
  }
  function fmtHour(h) {
    var ampm = h < 12 ? "AM" : "PM";
    var hr = h % 12 === 0 ? 12 : h % 12;
    return hr + ":00 " + ampm;
  }
  function fmtRange(start) {
    var end = new Date(start); end.setDate(end.getDate() + 6);
    var o = { month: "short", day: "numeric" };
    return start.toLocaleDateString(undefined, o) + " – "
         + end.toLocaleDateString(undefined, o) + ", " + end.getFullYear();
  }
  function esc(s) {
    return String(s).replace(/[&<>"']/g, function (c) {
      return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c];
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    var mount = document.getElementById("calendarGrid");
    if (!mount) return;

    var events = (window.CALENDAR_EVENTS || []).filter(function (e) { return e.date; });
    var label = document.getElementById("calendarRangeLabel");
    var prev = document.getElementById("calendarPrev");
    var next = document.getElementById("calendarNext");
    var cursor = startOfWeek(new Date());

    function render() {
      var today = new Date();
      if (label) label.textContent = fmtRange(cursor);

      var days = [];
      for (var i = 0; i < 7; i++) {
        var d = new Date(cursor); d.setDate(d.getDate() + i); days.push(d);
      }
      var names = ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"];

      var html = '<div class="cal-gridwrap">';

      // header row
      html += '<div class="cal-head"><div>Time</div>';
      days.forEach(function (d, i) {
        var isToday = sameDay(d, today);
        html += '<div class="' + (isToday ? 'cal-today-col' : '') + '">'
              + names[i] + '<strong>' + d.getDate() + '</strong></div>';
      });
      html += '</div>';

      // body
      html += '<div class="cal-body">';

      // time column
      html += '<div class="cal-timecol">';
      for (var h = DAY_START; h < DAY_END; h++) {
        html += '<div class="cal-hour">' + fmtHour(h) + '</div>';
      }
      html += '</div>';

      // day columns
      days.forEach(function (d) {
        var isToday = sameDay(d, today);
        html += '<div class="cal-daycol' + (isToday ? ' cal-today' : '') + '">';
        for (var h = DAY_START; h < DAY_END; h++) {
          html += '<div class="cal-slot"></div>';
        }

        events.filter(function (e) {
          var ed = parseDate(e.date);
          return ed && sameDay(ed, d);
        }).forEach(function (e) {
          var sm = toMinutes(e.startTime);
          var em = toMinutes(e.endTime);
          if (sm == null) return;
          if (em == null || em <= sm) em = sm + 60;
          var top = ((sm - DAY_START * 60) / 60) * SLOT_PX;
          var height = Math.max(((em - sm) / 60) * SLOT_PX - 2, 22);
          if (top < 0) { height += top; top = 0; }
          html += '<div class="cal-event cal-' + (e.status || "").toLowerCase() + '"'
                + ' style="top:' + top + 'px;height:' + height + 'px">'
                + '<span class="cal-ev-time">' + esc(e.startTime || "") + '</span>'
                + '<span class="cal-ev-room">' + esc(e.room || "") + '</span>'
                + (e.requestedBy ? '<span class="cal-ev-by">' + esc(e.requestedBy) + '</span>' : '')
                + '</div>';
        });

        html += '</div>';
      });

      html += '</div></div>';

      if (events.length === 0) {
        html += '<p class="cal-empty-note">No bookings to show. Approved bookings appear on the grid.</p>';
      }

      mount.innerHTML = html;
    }

    if (prev) prev.addEventListener("click", function () {
      cursor.setDate(cursor.getDate() - 7); render();
    });
    if (next) next.addEventListener("click", function () {
      cursor.setDate(cursor.getDate() + 7); render();
    });

    render();
  });
})();
