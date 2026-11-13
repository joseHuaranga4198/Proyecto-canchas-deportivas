/* La lógica del negocio y las agregaciones se ejecutan en Java/JDBC. */
(async function () {
  const contenedor = document.getElementById('graficos');
  const estado = document.getElementById('metricas-estado');
  try {
    const respuesta = await fetch(contenedor.dataset.endpoint, {headers: {'Accept':'application/json'}});
    if (!respuesta.ok || !respuesta.headers.get('content-type')?.includes('application/json')) throw new Error('consulta');
    const graficos = await respuesta.json();
    graficos.forEach((g, indice) => {
      const seccion = document.createElement('section'); seccion.className = 'panel detail-panel metrica';
      const titulo = document.createElement('h2'); titulo.textContent = g.titulo; seccion.append(titulo);
      const nota = document.createElement('p'); nota.className = 'muted'; nota.textContent = g.nota; seccion.append(nota);
      const marco = document.createElement('div'); marco.className = 'grafico-marco';
      const canvas = document.createElement('canvas'); canvas.setAttribute('role','img');canvas.setAttribute('aria-label',g.titulo);marco.append(canvas);seccion.append(marco);
      const sinDatos = document.createElement('p');sinDatos.textContent='No hay movimientos en el período seleccionado.';
      if (!g.valores.some(v => Number(v) !== 0)) seccion.append(sinDatos);
      const detalle=document.createElement('details');const resumen=document.createElement('summary');resumen.textContent='Ver valores del gráfico';detalle.append(resumen);
      const scroll=document.createElement('div');scroll.className='table-scroll';const tabla=document.createElement('table');
      const cabecera=document.createElement('tr');[g.ejeX,g.ejeY].forEach(t=>{const th=document.createElement('th');th.scope='col';th.textContent=t;cabecera.append(th);});const thead=document.createElement('thead');thead.append(cabecera);tabla.append(thead);
      const tbody=document.createElement('tbody');g.etiquetas.forEach((etiqueta,i)=>{const fila=document.createElement('tr');[etiqueta,g.valores[i]].forEach(t=>{const td=document.createElement('td');td.textContent=t;fila.append(td);});tbody.append(fila);});tabla.append(tbody);scroll.append(tabla);detalle.append(scroll);seccion.append(detalle);contenedor.append(seccion);
      if (typeof Chart === 'undefined') { marco.remove(); return; }
      new Chart(canvas, {type:g.tipo,data:{labels:g.etiquetas,datasets:[{label:g.ejeY,data:g.valores,borderColor:indice%2?'#157b62':'#2864dc',backgroundColor:indice%2?'#24a98788':'#2864dc99',borderWidth:2,fill:false,tension:0,pointRadius:g.etiquetas.length>60?0:3}]},options:{responsive:true,maintainAspectRatio:false,animation:false,plugins:{legend:{display:false}},scales:{x:{title:{display:true,text:g.ejeX},ticks:{maxTicksLimit:8},stacked:false},y:{beginAtZero:true,title:{display:true,text:g.ejeY},stacked:false,ticks:{precision:indice===1||indice===2?2:0}}}}});
    });
    estado.textContent = typeof Chart === 'undefined' ? 'La librería de gráficos no pudo cargarse. Puedes consultar las tablas.' : 'Cinco gráficos actualizados. Abre sus tablas para comprobar los valores.';
  } catch (error) { estado.textContent='No se pudieron cargar los gráficos. Comprueba tu sesión y vuelve a intentarlo.';estado.className='notice error'; }
})();
