<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Panel Administrativo</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 0; }
        .navbar { background-color: #1e293b; color: white; padding: 1rem 2rem; display: flex; justify-content: space-between; align-items: center; }
        .navbar h2 { margin: 0; font-size: 1.25rem; }
        .nav-links a { color: #cbd5e1; text-decoration: none; margin-left: 1.5rem; }
        .nav-links a:hover { color: white; }
        .container { max-width: 1100px; margin: 2rem auto; padding: 0 1rem; }

        .kpi-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 1.5rem; margin-bottom: 2rem; }
        .kpi-card { background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .kpi-title { color: #64748b; font-size: 0.9rem; text-transform: uppercase; font-weight: bold; }
        .kpi-val { font-size: 2rem; font-weight: bold; color: #0f172a; margin-top: 0.5rem; }

        .actions-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 1rem; margin-bottom: 2rem; }
        .action-card { background: white; padding: 1.2rem; border-radius: 8px; text-decoration: none; color: inherit; border-left: 4px solid #2563eb; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
        .action-card h3 { margin: 0 0 0.4rem 0; color: #1e293b; }
        .action-card p { margin: 0; font-size: 0.9rem; color: #64748b; }

        .table-section { background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
        table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
        th, td { text-align: left; padding: 0.75rem; border-bottom: 1px solid #e2e8f0; font-size: 0.9rem; }
        th { background-color: #f8fafc; color: #475569; }
        .badge-confirmada { background: #dcfce7; color: #15803d; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; font-size: 0.8rem; }
        .badge-cancelada { background: #fee2e2; color: #b91c1c; padding: 0.2rem 0.5rem; border-radius: 4px; font-weight: bold; font-size: 0.8rem; }
    </style>
</head>
<body>
    <div class="navbar">
        <h2>Administración | Canchas Deportivas</h2>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/admin/panel">Dashboard</a>
            <a href="${pageContext.request.contextPath}/canchas/admin">Canchas</a>
            <a href="${pageContext.request.contextPath}/admin/reservas">Reservas</a>
            <a href="${pageContext.request.contextPath}/usuario/admin/lista">Clientes</a>
            <a href="${pageContext.request.contextPath}/usuario/logout" style="color: #f87171;">Salir</a>
        </div>
    </div>

    <div class="container">
        <div class="kpi-grid">
            <div class="kpi-card">
                <div class="kpi-title">Canchas Totales</div>
                <div class="kpi-val">${totalCanchas}</div>
            </div>
            <div class="kpi-card">
                <div class="kpi-title">Reservas Registradas</div>
                <div class="kpi-val">${totalReservas}</div>
            </div>
            <div class="kpi-card">
                <div class="kpi-title">Clientes Registrados</div>
                <div class="kpi-val">${totalClientes}</div>
            </div>
        </div>

        <h3>Accesos Directos</h3>
        <div class="actions-grid">
            <a href="${pageContext.request.contextPath}/canchas/admin" class="action-card">
                <h3>Gestión de Canchas (I10)</h3>
                <p>Ver estado, registrar nuevas y modificar precios por hora.</p>
            </a>
            <a href="${pageContext.request.contextPath}/admin/reservas" class="action-card">
                <h3>Gestión de Reservas (I15)</h3>
                <p>Revisar agenda de turnos y gestionar cancelaciones.</p>
            </a>
            <a href="${pageContext.request.contextPath}/usuario/admin/lista" class="action-card">
                <h3>Gestión de Clientes (I14)</h3>
                <p>Consultar clientes activos y aplicar bloqueos.</p>
            </a>
        </div>

        <div class="table-section">
            <h3>Últimas Reservas en Sistema</h3>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Cliente</th>
                        <th>Cancha</th>
                        <th>Fecha</th>
                        <th>Horario</th>
                        <th>Total</th>
                        <th>Estado</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${reservasRecientes}">
                        <tr>
                            <td>#${r.id}</td>
                            <td>${r.clienteNombre}</td>
                            <td>${r.canchaNombre}</td>
                            <td>${r.fecha}</td>
                            <td>${r.horaInicio} - ${r.horaFin}</td>
                            <td>S/ ${r.montoTotal}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${r.estado == 'CONFIRMADA'}">
                                        <span class="badge-confirmada">${r.estado}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge-cancelada">${r.estado}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>