package com.gestaoeventos.apresentacao.inscricao;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestaoeventos.dominio.inscricao.inscricao.InscricaoServico;

@RestController
@RequestMapping("backend/inscricao")
class InscricaoController {

    @Autowired
    private InscricaoServico inscricaoServico;

    @RequestMapping(method = POST, path = "comprar")
    public ResponseEntity<String> comprarIngresso(@RequestBody InscricaoDto dto) {
        inscricaoServico.realizarInscricao(dto.cpfUsuario, dto.eventoId, dto.loteId);
        return ResponseEntity.ok("Inscrição confirmada com sucesso!");
    }

    static class InscricaoDto {
        public String cpfUsuario;
        public Long eventoId;
        public Long loteId;
    }
}