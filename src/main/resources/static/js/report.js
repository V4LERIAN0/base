document.addEventListener('DOMContentLoaded', () => {

    /* ---------- cached DOM refs ---------- */
    const reportBtn   = document.getElementById('fillReportBtn');
    const logsBtn     = document.getElementById('logsBtn');
    const albumId     = window.location.pathname.split('/')[2];

    const reportModal = document.getElementById('reportModal');
    const reportArea  = document.getElementById('reportArea');
    const saveDraftBtn   = document.getElementById('saveDraftBtn');
    const submitReportBtn = document.getElementById('submitReportBtn');

    /* ---------- utils ---------- */
    const disableFillBtn = () => reportBtn?.setAttribute('disabled', 'true');

    /* POST helper (x-www-form-urlencoded) */
    const postForm = (url, bodyObj) =>
        fetch(url, {
            method  : 'POST',
            headers : { 'Content-Type': 'application/x-www-form-urlencoded' },
            body    : new URLSearchParams(bodyObj)
        }).then(r => r.text());   // always return plain text

    /* ---------- 1. open “Fill project report” ---------- */
    reportBtn?.addEventListener('click', () => {
        new bootstrap.Modal(reportModal).show();
    });

    /* ---------- 2. Save draft ---------- */
    saveDraftBtn?.addEventListener('click', () => {
        const contenido = reportArea.value.trim();
        if (!contenido) return alert('Draft is empty');

        postForm(`/albumes/${albumId}/reports/draft`, { contenido })
            .then(txt => {
                if (txt === 'ALREADY_SENT') {
                    alert('A report has already been sent for today - try again tomorrow');
                    disableFillBtn();
                } else {
                    alert('Draft saved ✔');
                }
            })
            .catch(() => alert('Error while saving draft'));
    });

    /* ---------- 3. Send final report ---------- */
    submitReportBtn?.addEventListener('click', () => {
        const contenido = reportArea.value.trim();
        if (!contenido) return alert('Report is empty');

        postForm(`/albumes/${albumId}/reports/send`, { contenido })
            .then(txt => {
                if (txt === 'ALREADY_SENT') {
                    alert('A report has already been sent for today - try again tomorrow');
                    disableFillBtn();                    // also grey-out here
                } else {
                    alert('Report sent ✔');
                    console.log('modal instance:', bootstrap.Modal.getOrCreateInstance(reportModal));
                    bootstrap.Modal.getOrCreateInstance(reportModal).hide();
                    disableFillBtn();
                }
            })
    });

    /* ---------- 4. Open logs (sent reports only) ---------- */
    logsBtn?.addEventListener('click', () => {
        fetch(`/albumes/${albumId}/reports`)
            .then(r => {
                if (!r.ok) throw new Error('HTTP ' + r.status);
                return r.text();
            })
            .then(html => {
                /* remove any old modal and insert the fresh one */
                document.getElementById('logsModal')?.remove();
                document.body.insertAdjacentHTML('beforeend', html);
                new bootstrap.Modal(document.getElementById('logsModal')).show();
            })
            .catch(err => console.error('Could not load logs:', err));
    });
});

/* ---------- admin inline edit inside logs ---------- */
function saveEditedReport(btn) {
    const id    = btn.dataset.id;
    const txt   = document.getElementById('txt_' + id).value.trim();
    const album = window.location.pathname.split('/')[2];

    fetch(`/albumes/${album}/reports/${id}/edit`, {
        method  : 'POST',
        headers : { 'Content-Type': 'application/x-www-form-urlencoded' },
        body    : new URLSearchParams({ contenido: txt })
    }).then(() => alert('Saved ✔'))
        .catch(() => alert('Error while saving changes'));
}
