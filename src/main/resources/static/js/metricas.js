/* Las consultas y los cálculos se hacen en Java/JDBC; aquí solo se dibujan los resultados. */
(async function () {
  const series = document.getElementById('graficos');
  const indicadores = document.getElementById('indicadores');
  const estado = document.getElementById('metricas-estado');

  function crear(etiqueta, clase, texto) {
    const elemento = document.createElement(etiqueta);
    if (clase) elemento.className = clase;
    if (texto !== undefined) elemento.textContent = texto;
    return elemento;
  }

  function tablaDeValores(g) {
    const detalle = crear('details');
    detalle.append(crear('summary', null, 'Ver valores del gráfico'));
    const scroll = crear('div', 'table-scroll');
    const tabla = crear('table');
    const cabecera = crear('tr');
    [g.ejeX, g.ejeY].forEach(t => { const th = crear('th', null, t); th.scope = 'col'; cabecera.append(th); });
    const thead = crear('thead'); thead.append(cabecera); tabla.append(thead);
    const tbody = crear('tbody');
    g.etiquetas.forEach((etiqueta, i) => {
      const fila = crear('tr');
      fila.append(crear('td', null, etiqueta), crear('td', null, g.valores[i]));
      tbody.append(fila);
    });
    tabla.append(tbody); scroll.append(tabla); detalle.append(scroll);
    return detalle;
  }

  function dibujarSerie(g, indice) {
    const seccion = crear('section', 'panel detail-panel metrica');
    seccion.append(crear('h2', null, g.titulo), crear('p', 'muted', g.nota));
    const marco = crear('div', 'grafico-marco');
    const canvas = crear('canvas'); canvas.setAttribute('role', 'img'); canvas.setAttribute('aria-label', g.titulo);
    marco.append(canvas); seccion.append(marco);
    if (!g.valores.some(v => Number(v) !== 0)) seccion.append(crear('p', null, 'No hay movimientos en el período seleccionado.'));
    seccion.append(tablaDeValores(g));
    series.append(seccion);
    if (typeof Chart === 'undefined') { marco.remove(); return; }
    const color = indice % 2 ? '#157b62' : '#2864dc';
    new Chart(canvas, {
      type: 'line',
      data: { labels: g.etiquetas, datasets: [{ label: g.ejeY, data: g.valores, borderColor: color, borderWidth: 2,
        fill: false, tension: 0, pointRadius: g.etiquetas.length > 60 ? 0 : 3 }] },
      options: { responsive: true, maintainAspectRatio: false, animation: false, plugins: { legend: { display: false } },
        scales: { x: { title: { display: true, text: g.ejeX }, ticks: { maxTicksLimit: 8 } },
                  y: { beginAtZero: true, title: { display: true, text: g.ejeY } } } }
    });
  }

  function dibujarIndicador(g) {
    const tarjeta = crear('section', 'panel kpi');
    tarjeta.append(crear('h2', null, g.titulo));
    tarjeta.append(crear('p', 'kpi-valor', g.valores[0]));
    tarjeta.append(crear('p', 'kpi-unidad', g.ejeY));
    tarjeta.append(crear('p', 'muted', g.nota));
    indicadores.append(tarjeta);
  }

  try {
    const respuesta = await fetch(series.dataset.endpoint, { headers: { 'Accept': 'application/json' } });
    if (!respuesta.ok) throw new Error('consulta');
    const metricas = await respuesta.json();
    let numeroSerie = 0;
    metricas.forEach(g => {
      if (g.tipo === 'kpi') dibujarIndicador(g);
      else dibujarSerie(g, numeroSerie++);
    });
    estado.textContent = 'Diez métricas actualizadas: cinco series de tiempo y cinco de estado puntual.';
  } catch (error) {
    estado.textContent = 'No se pudieron cargar las métricas. Comprueba tu sesión y vuelve a intentarlo.';
    estado.className = 'notice error';
  }
})();
