package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final ConcurrentHashMap<Integer, Candidate> candidates = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Candidate1", "description1", LocalDateTime.now()));
        save(new Candidate(1, "Candidate2", "description2", LocalDateTime.now()));
        save(new Candidate(2, "Candidate3", "description3", LocalDateTime.now()));
        save(new Candidate(3, "Candidate4", "description4", LocalDateTime.now()));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.incrementAndGet());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate) ->
                new Candidate(
                        oldCandidate.getId(),
                        candidate.getName(),
                        candidate.getDescription(),
                        candidate.getCreationDate())
        ) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}