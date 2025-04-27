document.addEventListener('DOMContentLoaded', () => {

    /* ---- fill report modal ---- */
    const reportBtn   = document.getElementById('fillReportBtn');
    const logsBtn     = document.getElementById('logsBtn');
    const albumId     = window.location.pathname.split('/')[2]; // /albumes/{id}

    if (reportBtn) {
        reportBtn.addEventListener('click', () =>
            bootstrap.Modal.getOrCreateInstance('#reportModal').show()
        );
    }

    /* save draft / send */
    const postJson = (url, body) => fetch(url, {
        method : 'POST',
        headers: { 'Content-Type':'application/x-www-form-urlencoded' },
        body   : new URLSearchParams(body)
    });

    document.getElementById('saveDraftBtn')?.addEventListener('click', () => {
        const contenido = document.getElementById('reportArea').value;
        postJson(`/albumes/${albumId}/reports/draft`, { contenido })
            .then(() => alert('Draft saved ✔'));
    });

    document.getElementById('submitReportBtn')?.addEventListener('click', () => {
        const contenido = document.getElementById('reportArea').value;
        postJson(`/albumes/${albumId}/reports/send`, { contenido })
            .then(r => r.text())
            .then(txt => {
                if (txt === 'ALREADY_SENT') {
                    alert('Ya enviaste un reporte hoy - espera a mañana.');
                } else {
                    alert('Reporte enviado ✔');
                    bootstrap.Modal.getOrCreateInstance('#reportModal').hide();
                }
            });
    });

    /* ---- logs ---- */
    logsBtn?.addEventListener('click', () => {
        fetch(`/albumes/${albumId}/reports`)
            .then(r => r.text())
            .then(html => {
                document.getElementById('logsModalContainer').innerHTML = html;
                bootstrap.Modal.getOrCreateInstance('#logsModal').show();
            });
    });
});

/* admin only – inline save inside logs */
function saveEditedReport(btn){
    const id   = btn.getAttribute('data-id');
    const txt  = document.getElementById('txt_'+id).value;
    const album = window.location.pathname.split('/')[2];
    fetch(`/albumes/${album}/reports/${id}/edit`,{
        method:'POST',
        headers:{'Content-Type':'application/x-www-form-urlencoded'},
        body:new URLSearchParams({contenido:txt})
    }).then(()=>alert('Saved ✔'));
}
