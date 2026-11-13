const dialogoCancelacion = document.querySelector('#cancelar-reserva');

if (typeof dialogoCancelacion?.showModal === 'function') {
  const formulario = dialogoCancelacion.querySelector('form');
  const resumen = dialogoCancelacion.querySelector('#cancelar-resumen');
  const conservar = formulario.querySelector('button[type="button"]');

  conservar.addEventListener('click', () => dialogoCancelacion.close());

  for (const boton of document.querySelectorAll('[data-cancelar-url]')) {
    boton.addEventListener('click', () => {
      formulario.action = boton.dataset.cancelarUrl;
      resumen.textContent = boton.dataset.reserva;
      dialogoCancelacion.showModal();
    });
    boton.disabled = false;
  }
}
