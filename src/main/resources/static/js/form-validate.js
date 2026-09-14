/* ============================================================
   form-validate.js  —  JavaScript form validation
   Replaces the browser's built-in (HTML "required") validation.

   Usage:
     <form class="js-validate" data-form="booking">   (data-form optional)
       <input data-validate data-required data-label="Email" data-type="email">

   Field attributes (all optional except data-validate):
     data-validate            include this field in validation
     data-required            must not be empty / must be checked
     data-label="Full Name"   name used in messages (else the <label> text)
     data-type="email|number|time|date"
     data-min="1"             minimum numeric value
     data-minlength="8"       minimum text length
     data-match="#password"   value must equal another field
     data-message="..."       override the "is required" message
   ============================================================ */
(function () {
  "use strict";

  function labelFor(f) {
    if (f.getAttribute("data-label")) return f.getAttribute("data-label");
    if (f.labels && f.labels[0]) return f.labels[0].textContent.replace(/\*/g, "").trim();
    return f.name || "This field";
  }

  function fieldError(f) {
    var isCheckbox = f.type === "checkbox";
    var v = isCheckbox ? "" : (f.value || "").trim();
    var label = labelFor(f);
    var required = f.hasAttribute("data-required");

    if (isCheckbox && required && !f.checked) {
      return f.getAttribute("data-message") || "You must accept the " + label + ".";
    }
    if (required && v === "") {
      return f.getAttribute("data-message") || label + " is required.";
    }
    if (v === "") return null; // optional and empty

    var type = f.getAttribute("data-type");
    if (type === "email" && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)) {
      return "Enter a valid email address.";
    }
    if (type === "number") {
      var n = Number(v);
      if (isNaN(n)) return label + " must be a number.";
      var min = f.getAttribute("data-min");
      if (min !== null && min !== undefined && n < Number(min)) {
        return label + " must be at least " + min + ".";
      }
    }
    var ml = f.getAttribute("data-minlength");
    if (ml && v.length < Number(ml)) {
      return label + " must be at least " + ml + " characters.";
    }
    var matchSel = f.getAttribute("data-match");
    if (matchSel) {
      var other = document.querySelector(matchSel);
      if (other && v !== other.value) {
        return f.getAttribute("data-match-message") || label + " does not match.";
      }
    }
    return null;
  }

  function anchorOf(f) {
    return f.closest(".form-group, .modal-field, .input-wrapper, .recurring-row, .panel-field, .terms-box")
        || f.parentElement;
  }

  function showError(f, msg) {
    clearError(f);
    f.classList.add("jsv-invalid");
    f.setAttribute("aria-invalid", "true");
    var e = document.createElement("div");
    e.className = "jsv-error";
    e.textContent = msg;
    var a = anchorOf(f);
    if (a) a.appendChild(e);
  }

  function clearError(f) {
    f.classList.remove("jsv-invalid");
    f.removeAttribute("aria-invalid");
    var a = anchorOf(f);
    if (a) {
      var errs = a.querySelectorAll(".jsv-error");
      for (var i = 0; i < errs.length; i++) errs[i].remove();
    }
  }

  function bookingChecks(form, showFn, firstRef) {
    var start = form.querySelector("#startTime, [name='startTime']");
    var end = form.querySelector("#endTime, [name='endTime']");
    if (start && end && start.value && end.value && end.value <= start.value) {
      showFn(end, "End time must be after the start time.");
      if (!firstRef.f) firstRef.f = end;
    }
    var room = form.querySelector("#room, [name='room']");
    var att = form.querySelector("#numberOfAttendees, [name='numberOfAttendees']");
    if (room && att && att.value && room.options) {
      var opt = room.options[room.selectedIndex];
      var cap = opt ? parseInt(opt.getAttribute("data-capacity"), 10) : NaN;
      if (!isNaN(cap) && parseInt(att.value, 10) > cap) {
        showFn(att, "Number of attendees (" + att.value +
          ") exceeds the room capacity of " + cap + ".");
        if (!firstRef.f) firstRef.f = att;
      }
    }
  }

  function validateForm(form) {
    var firstRef = { f: null };
    var fields = form.querySelectorAll("[data-validate]");
    for (var i = 0; i < fields.length; i++) {
      var msg = fieldError(fields[i]);
      if (msg) {
        showError(fields[i], msg);
        if (!firstRef.f) firstRef.f = fields[i];
      } else {
        clearError(fields[i]);
      }
    }
    if (form.getAttribute("data-form") === "booking") {
      bookingChecks(form, showError, firstRef);
    }
    return firstRef.f;
  }

  document.addEventListener("DOMContentLoaded", function () {
    var forms = document.querySelectorAll("form.js-validate");
    for (var i = 0; i < forms.length; i++) {
      (function (form) {
        form.setAttribute("novalidate", "novalidate");

        form.addEventListener("submit", function (e) {
          var bad = validateForm(form);
          if (bad) {
            e.preventDefault();
            e.stopPropagation();
            try {
              bad.focus();
              bad.scrollIntoView({ behavior: "smooth", block: "center" });
            } catch (ignore) {}
          }
        });

        var fields = form.querySelectorAll("[data-validate]");
        for (var j = 0; j < fields.length; j++) {
          (function (field) {
            field.addEventListener("blur", function () {
              var msg = fieldError(field);
              if (msg) showError(field, msg); else clearError(field);
            });
            field.addEventListener("input", function () {
              if (field.classList.contains("jsv-invalid") && !fieldError(field)) {
                clearError(field);
              }
            });
            field.addEventListener("change", function () {
              if (field.classList.contains("jsv-invalid") && !fieldError(field)) {
                clearError(field);
              }
            });
          })(fields[j]);
        }
      })(forms[i]);
    }
  });

  window.JSValidate = { validateForm: validateForm, showError: showError, clearError: clearError };
})();
