import React, { useEffect, useMemo, useState } from "react";
import { ArtistSummary } from "../models";

type Props = {
  availableArtists: ArtistSummary[];
  selectedArtistIds: number[];
  onChange: (ids: number[]) => void;
  disabled?: boolean;
  error?: string;
};

const ArtistPicker: React.FC<Props> = ({
  availableArtists,
  selectedArtistIds,
  onChange,
  disabled = false,
  error,
}) => {
  const [query, setQuery] = useState("");
  const [isOpen, setIsOpen] = useState(false);

  // artistas selecionados (objetos)
  const selectedArtists = useMemo(() => {
    const setIds = new Set(selectedArtistIds);
    return availableArtists.filter((a) => setIds.has(a.id));
  }, [availableArtists, selectedArtistIds]);

  // resultados filtrados (não selecionados ainda)
  const filteredArtists = useMemo(() => {
    const q = query.trim().toLowerCase();

    return availableArtists
      .filter((a) => !selectedArtistIds.includes(a.id))
      .filter((a) => (q ? a.name.toLowerCase().includes(q) : true))
      .slice(0, 10); // limita dropdown
  }, [availableArtists, selectedArtistIds, query]);

  const addArtist = (artistId: number) => {
    if (selectedArtistIds.includes(artistId)) return;
    onChange([...selectedArtistIds, artistId]);
    setQuery("");
    setIsOpen(false);
  };

  const removeArtist = (artistId: number) => {
    onChange(selectedArtistIds.filter((id) => id !== artistId));
  };

  // fecha dropdown ao clicar fora
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      const target = e.target as HTMLElement;
      if (!target.closest("[data-artist-picker]")) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  return (
    <div data-artist-picker className="space-y-2">
      {/* input de busca */}
      <div className="relative">
        <input
          type="text"
          value={query}
          onChange={(e) => {
            setQuery(e.target.value);
            setIsOpen(true);
          }}
          onFocus={() => setIsOpen(true)}
          placeholder="Pesquisar artista pelo nome..."
          disabled={disabled}
          className={`w-full px-4 py-2 border ${
            error ? "border-red-500" : "border-gray-300"
          } rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500`}
        />

        {/* dropdown */}
        {isOpen && (
          <div className="absolute z-20 mt-2 w-full bg-white border border-gray-200 rounded-lg shadow-lg max-h-60 overflow-auto">
            {filteredArtists.length > 0 ? (
              filteredArtists.map((artist) => (
                <button
                  key={artist.id}
                  type="button"
                  onClick={() => addArtist(artist.id)}
                  disabled={disabled}
                  className="w-full text-left px-4 py-2 hover:bg-gray-50 flex items-center justify-between"
                >
                  <span className="text-gray-800">{artist.name}</span>
                  <span className="text-sm text-primary-600 font-semibold">
                    Adicionar
                  </span>
                </button>
              ))
            ) : (
              <div className="px-4 py-3 text-sm text-gray-500">
                {availableArtists.length === 0
                  ? "Nenhum artista cadastrado."
                  : "Nenhum resultado encontrado."}
              </div>
            )}
          </div>
        )}
      </div>

      {/* chips selecionados */}
      <div className="border border-gray-300 rounded-lg p-3 min-h-[56px] bg-white">
        {selectedArtists.length > 0 ? (
          <div className="flex flex-wrap gap-2">
            {selectedArtists.map((artist) => (
              <span
                key={artist.id}
                className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary-50 text-primary-700 border border-primary-200 text-sm"
              >
                {artist.name}
                <button
                  type="button"
                  onClick={() => removeArtist(artist.id)}
                  disabled={disabled}
                  className="hover:text-red-600 font-bold"
                  aria-label={`Remover ${artist.name}`}
                >
                  ×
                </button>
              </span>
            ))}
          </div>
        ) : (
          <p className="text-gray-500 text-sm">
            Nenhum artista selecionado ainda.
          </p>
        )}
      </div>

      {error && <p className="text-sm text-red-600">{error}</p>}
    </div>
  );
};

export default ArtistPicker;
