import { Inter } from "next/font/google";
import { useEffect, useRef, useState } from "react";
var he = require("he");
const inter = Inter({ subsets: ["latin"] });

type Question = {
  answers: string[];
  category: string;
  difficulty: string;
  question: string;
  type: string;
};

export default function Home() {
  const API_URL = process.env.NEXT_PUBLIC_API_URL;

  const [questions, setQuestions] = useState<Question[]>([]);
  const [givenAnswers, setAnswer] = useState<string[]>([]);
  const [correctAnswer, setCorrectAnswer] = useState<boolean[]>([]);

  const elToScroll = useRef<HTMLDivElement>(null);

  useEffect(() => {
    fetchQuestions();
  }, []);

  useEffect(() => {
    if (elToScroll.current) {
      elToScroll.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [givenAnswers]);


  const newGame = async () => {
    setQuestions([]);
    setAnswer([]);
    setCorrectAnswer([]);
    fetchQuestions();
  };

  const fetchQuestions = async () => {
    try {
      const response = await fetch(`${API_URL}/questions`, {
        method: "GET",
        credentials: "include",
      });
      const data: Question[] = await response.json();
      setQuestions(data);
    } catch (error) {
      console.error("Error fetching questions:", error);
    }
  };

  const fetchAnswers = async () => {
    try {
      const response = await fetch(`${API_URL}/answers`, {
        method: "POST",
        body: JSON.stringify(givenAnswers),
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // Send cookies along with the request
      });
      if (!response.ok) return;
      const data: boolean[] = await response.json();
      setCorrectAnswer(data);
    } catch (error) {
      console.error("Error fetching questions:", error);
    }
  };

  return (
    <main
      className={`flex min-h-screen max-w-screen-lg m-auto flex-col  ${inter.className}`}
    >
      <h1 className="mb-5 text-5xl p-5 font-medium text-gray-900 dark:text-white">
        {" "}
        Quad Solutions technical assignment
      </h1>
      {questions.map((q, i) => (
        <div
          className={
            givenAnswers.length >= i
              ? `flex grow-0 flex-col flex-1 p-6 transition-opacity  `
              : "opacity-0 hidden"
          }
          key={i}
        >
          <h3 className="mb-5 text-lg font-medium text-gray-900 dark:text-white">
            {he.decode(q.question)}
          </h3>
          <ul className="grid w-full gap-6 md:grid-cols-2">
            {q.answers.map((a, ai) => (
              <li key={ai}>
                <input
                  type="radio"
                  disabled={correctAnswer.length > 0}
                  id={a}
                  name={q.question}
                  value={a}
                  className="hidden peer"
                  required
                  onClick={() => {
                    const newAnswers = [...givenAnswers];
                    newAnswers[i] = a;
                    setAnswer(newAnswers);
                  }}
                />
                <label
                  htmlFor={a}
                  className={`inline-flex items-center justify-between w-full p-5 text-gray-500 bg-white border border-gray-200 rounded-lg cursor-pointer dark:hover:text-gray-300 dark:border-gray-700 hover:text-gray-600 hover:bg-gray-100 dark:text-gray-400 dark:bg-gray-800 dark:hover:bg-gray-700 ${
                    correctAnswer[i] == null
                      ? "dark:peer-checked:text-blue-500 peer-checked:border-blue-600 peer-checked:text-blue-600"
                      : correctAnswer[i]
                      ? " dark:peer-checked:text-green-500 peer-checked:border-green-600 peer-checked:text-green-600"
                      : "dark:peer-checked:text-red-500 peer-checked:border-red-600 peer-checked:text-red-600"
                  }    `}
                >
                  <div className="block">
                    <div className="w-full text-lg font-semibold">
                      {he.decode(a)}
                    </div>
                  </div>
                  {givenAnswers[i] == a && (
                    <div>
                      {correctAnswer[i] && (
                        <svg
                          className="w-5 h-5 ml-3"
                          aria-hidden="true"
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 14 10"
                        >
                          <path
                            stroke="currentColor"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth="2"
                            d="M1 5l3 3 7-7"
                          />
                        </svg>
                      )}
                      {correctAnswer[i] === false && (
                        <svg
                          className="w-5 h-5 ml-3"
                          aria-hidden="true"
                          xmlns="http://www.w3.org/2000/svg"
                          fill="none"
                          viewBox="0 0 14 14"
                        >
                          <path
                            stroke="currentColor"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth="2"
                            d="M1 1l12 12M13 1L1 13"
                          />
                        </svg>
                      )}
                    </div>
                  )}
                </label>
              </li>
            ))}
          </ul>
        </div>
      ))}

      {givenAnswers.length == 5 && (
        <div className="flex flex-1 flex-row justify-center">
          {correctAnswer.length > 0 ? (
            <button
              onClick={newGame}
              className="text-blue-700 hover:text-white border border-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 rounded-lg text-lg font-semibold px-5 py-5 text-center mr-2 mb-2 dark:border-blue-500 dark:text-blue-500 dark:hover:text-white dark:hover:bg-blue-600 dark:focus:ring-blue-800 w-3/6"
            >
              Load new questions
            </button>
          ) : (
            <button
              type="button"
              onClick={fetchAnswers}
              className="text-green-700 hover:text-white border border-green-700 hover:bg-green-800 focus:ring-4 focus:outline-none focus:ring-green-300 rounded-lg text-lg font-semibold px-5 py-5 text-center mr-2 mb-2 dark:border-green-500 dark:text-green-500 dark:hover:text-white dark:hover:bg-green-600 dark:focus:ring-green-800 w-3/6"
            >
              Check
            </button>
          )}
        </div>
      )}
      <div ref={elToScroll}></div>
    </main>
  );
}
