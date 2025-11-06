package uir.ac.ma.suivi_marches.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uir.ac.ma.suivi_marches.model.Commentaire;
import uir.ac.ma.suivi_marches.repository.CommentaireRepo;

import java.util.List;
import java.util.Optional;

@Service
public class CommentaireService {
    CommentaireRepo commentaireRepo;

    @Autowired
    public CommentaireService(CommentaireRepo commentaireRepo) {
        this.commentaireRepo = commentaireRepo;
    }

    public List<Commentaire> getAllCommentaires() {
        return commentaireRepo.findAll();
    }

    public Optional<Commentaire> getCommentaireById(int idCommentaire) {
        return commentaireRepo.findById(idCommentaire);
    }

    @Transactional
    public Commentaire addCommentaire(Commentaire commentaire) {
        return commentaireRepo.save(commentaire);
    }

    @Transactional
    public Commentaire modifyCommentaire(Commentaire commentaire) {
        return commentaireRepo.save(commentaire);
    }

    @Transactional
    public void deleteCommentaire(int idCommentaire) {
        commentaireRepo.deleteById(idCommentaire);
    }
}