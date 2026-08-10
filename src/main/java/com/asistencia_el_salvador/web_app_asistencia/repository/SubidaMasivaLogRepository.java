package com.asistencia_el_salvador.web_app_asistencia.repository;

import com.asistencia_el_salvador.web_app_asistencia.model.MensajeError;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class SubidaMasivaLogRepository {

    private final SimpleJdbcCall insertLogCall;
    private final SimpleJdbcCall insertDetalleCall;
    private final JdbcTemplate jdbcTemplate;

    public SubidaMasivaLogRepository(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        this.insertLogCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_insertar_subida_masiva_log");

        this.insertDetalleCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("sp_insertar_subida_masiva_log_detalle");
    }

    /** Inserta el encabezado y devuelve el idSubida generado */
    public Integer registrarLog(int totalProcesados, int totalNoProcesados,
                                String nit, String procesadoPor) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_totalProcesados", totalProcesados)
                .addValue("p_totalNoProcesados", totalNoProcesados)
                .addValue("p_NIT", nit)
                .addValue("p_procesadoPor", procesadoPor);

        Map<String, Object> resultado = insertLogCall.execute(params);
        return (Integer) resultado.get("p_idSubida");
    }

    /** Opción A: inserta un detalle llamando al SP (uno por uno) */
    public void registrarDetalle(int idSubida, Integer fila, String identificacion, String mensajeError) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_idSubida", idSubida)
                .addValue("p_fila", fila)
                .addValue("p_identificacion", identificacion)
                .addValue("p_mensajeError", mensajeError);

        insertDetalleCall.execute(params);
    }

    /** Opción B (recomendada si hay muchos errores): batch insert directo, sin SP */
    public void registrarDetallesBatch(int idSubida, List<MensajeError> errores) {
        String sql = "INSERT INTO subida_masiva_log_detalle (idSubida, fila, identificacion, mensajeError) " +
                "VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, errores, errores.size(), (ps, error) -> {
            ps.setInt(1, idSubida);
            ps.setObject(2, error.getFila(), Types.INTEGER);
            ps.setString(3, error.getIdentificacion());
            ps.setString(4, error.getMensaje());
        });
    }
}